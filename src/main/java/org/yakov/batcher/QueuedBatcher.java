package org.yakov.batcher;

import org.slf4j.Logger;
import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;
import org.yakov.jobs.JobResultCreator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class QueuedBatcher<I, J, T, R> implements Batcher<I, J, T, R> {
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
        // A scheduled executor operating at a fixed rate ensures
        // a single, periodic thread that submits batches.
        executor.scheduleAtFixedRate(
                submitBatch(),
                INITIAL_DELAY,
                props.getTimeSliceMillis(),
                TimeUnit.MILLISECONDS
        );
        getLogger().info("Initialised batcher with time-slice {}, wait-time {}, batch-size {}, queue capacity {}",
                props.getTimeSliceMillis(),
                props.getSubmitWaitTimeMillis(),
                props.getBatchSize(),
                props.getQueueCapacity());
    }

    @Override
    public JobResult<J, R> submit(Job<I, T> job) {
        counters.incrementSubmitted(BigInteger.ONE);
        getLogger().trace("Submitted job {}", job.getId());

        if (executor.isShutdown()) {
            counters.incrementFailed(BigInteger.ONE);
            getLogger().error("Batcher shut down. Rejecting job {}", job.getId());
            return jobResultCreator.shutdownResult(job);
        }

        var pending = jobResultCreator.pendingResult(job);
        try {
            if (queue.offer(pending, props.getSubmitWaitTimeMillis(), TimeUnit.MILLISECONDS)) {
                getLogger().trace("Enqueued job {}", job.getId());
                return jobResultCreator.pendingResult(job);
            } else {
                counters.incrementFailed(BigInteger.ONE);
                getLogger().trace("Failed to enqueue job {}", job.getId());
                return jobResultCreator.errorResult(job, JobResult.Error.SUBMIT_FAILED);
            }

        } catch (InterruptedException e) {
            // If the thread is interrupted, we should gracefully shut down
            getLogger().error("Interrupted exception. Shutting down batcher", e);
            getLogger().trace("Batcher shutting down. Rejecting job {}", job.getId());
            shutdown();
            counters.incrementFailed(BigInteger.ONE);
            return jobResultCreator.errorResult(job, JobResult.Error.SUBMIT_FAILED);
        }
    }

    @Override
    public void shutdown() {
        getLogger().info("Shutting down");
        executor.shutdown();
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public Counters getCounters() {
        return counters;
    }

    /*
    This is the main job submission logic.
    At each tick of the timer, take up to `batchSize` number of jobs
    from the queue and process them.
     */
    private Runnable submitBatch() {
        return () -> {
            var jobs = new ArrayList<JobResult<J, R>>(props.getBatchSize());
            var dequeued = queue.drainTo(jobs, props.getBatchSize());
            getLogger().trace("Sending {} jobs to processor", dequeued);
            batchProcessor.process(jobs);

            counters.incrementProcessed(BigInteger.valueOf(dequeued));
        };
    }

    abstract protected Logger getLogger();
}