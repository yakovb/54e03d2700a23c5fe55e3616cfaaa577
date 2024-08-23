package org.yakov.batcher;

import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;

/**
 * Allows submitting {@link Job}s to be processed by an underlying processor.
 * Exposes {@link Counters} to observe important aspects of its operation.
 * Once shutdown, will no longer accept jobs but will process jobs previously submitted.
 * Rejected jobs will have an associated {@link org.yakov.jobs.JobResult.Error}.
 *
 * @param <I> Type identifying the ID of a {@link Job}.
 * @param <J> Type identifying the ID of a {@link JobResult}.
 * @param <T> Type identifying the payload of a {@link Job}.
 * @param <R> Type identifying the payload of a {@link JobResult}.
 */
public interface Batcher<I, J, T, R> {

    /**
     * Submit a {@link Job} to be processed. If this Batcher is shutdown, will return without processing the job.
     *
     * @return {@link JobResult}
     */
    JobResult<J, R> submit(Job<I, T> job);

    /**
     * Shutdown this Batcher. Will not process jobs submitted after shutdown is called.
     */
    void shutdown();

    /**
     * @return {@link Counters} exposing observability info.
     */
    Counters getCounters();
}