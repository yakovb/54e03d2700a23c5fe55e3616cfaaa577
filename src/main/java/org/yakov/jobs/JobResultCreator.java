package org.yakov.jobs;

/**
 * Since all {@link Job}s and {@link JobResult}s are specific to a set of types, this class provides
 * convenience methods for creating the main varieties of {@link JobResult}s.
 *
 * @param <I> type of the {@link Job} identifier
 * @param <J> type of the {@link JobResult} identifier
 * @param <T> type of the {@link Job} payload
 * @param <R> type of the {@link JobResult} payload
 */
public interface JobResultCreator<I, J, T, R> {
    /**
     * Create a result indicating a failed job due to submitting a job to a {@link org.yakov.batcher.Batcher}
     * that was shut down.
     */
    JobResult<J, R> shutdownResult(Job<I, T> job);

    JobResult<J, R> pendingResult(Job<I, T> job);

    JobResult<J, R> errorResult(Job<I, T> job, JobResult.Error error);

    JobResult<J, R> successResult(Job<I, T> job, R result);
}
