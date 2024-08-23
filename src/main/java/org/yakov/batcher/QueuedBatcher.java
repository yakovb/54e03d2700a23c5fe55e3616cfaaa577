package org.yakov.batcher;

import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;
import org.yakov.jobs.JobResultCreator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class QueuedBatcher<I, J, T, R> implements Batcher<I, J, T, R> {
    //TODO logger
    private static final long INITIAL_DELAY = 0L;

    private final BatchProcessor<J, R> batchProcessor;
    private final ArrayBlockingQueue<JobResult<J, R>> queue;
    private final ScheduledExecutorService executor;
    private final JobResultCreator<I, J, T, R> jobResultCreator;
    private final BatcherProperties props;
    private final Counters counters;

    public QueuedBatcher(BatchProcessor<J, R> batchProcessor,
                         JobResultCreator<I, J, T, R> jobResultCreator,
                         BatcherProperties props,
                         Counters counters) {
        this.batchProcessor = batchProcessor;
        this.queue = new ArrayBlockingQueue<>(props.getQueueCapacity());
        this.props = props;
        this.jobResultCreator = jobResultCreator;
        this.counters = counters;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        init();
    }

    private void init() {
        executor.scheduleAtFixedRate(
                submitBatch(),
                INITIAL_DELAY,
                props.getTimeSliceMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    //TODO
    @Override
    public JobResult<J, R> submit(Job<I, T> job) {
        counters.incrementSubmitted(BigInteger.ONE);
        if (executor.isShutdown()) {
            counters.incrementFailed(BigInteger.ONE);
            return jobResultCreator.shutdownResult(job);
        }
        var pending = jobResultCreator.pendingResult(job);
        try {
            if (queue.offer(pending, props.getSubmitWaitTimeMillis(), TimeUnit.MILLISECONDS)) {
                return jobResultCreator.pendingResult(job);
            } else {
                counters.incrementFailed(BigInteger.ONE);
                return jobResultCreator.errorResult(job, JobResult.Error.SUBMIT_FAILED);
            }

        } catch (InterruptedException e) {
            shutdown();
            counters.incrementFailed(BigInteger.ONE);
            return jobResultCreator.errorResult(job, JobResult.Error.SUBMIT_FAILED);
        }
    }

    //TODO
    @Override
    public void shutdown() {
        executor.shutdown();
    }

    //TODO
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public Counters getCounters() {
        return counters;
    }

    private Runnable submitBatch() {
        return () -> {
            var jobs = new ArrayList<JobResult<J, R>>(props.getBatchSize());
            var postProcessCount = queue.drainTo(jobs, props.getBatchSize());
            batchProcessor.process(jobs);

            counters.incrementProcessed(BigInteger.valueOf(postProcessCount));
        };
    }

    abstract protected Logger getLogger();
}