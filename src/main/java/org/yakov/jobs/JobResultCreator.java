package org.yakov.jobs;

public interface JobResultCreator<I, J, T, R> {
    JobResult<J, R> shutdownResult(Job<I, T> job);

    JobResult<J, R> pendingResult(Job<I, T> job);

    JobResult<J, R> errorResult(Job<I, T> job, JobResult.Error error);

    JobResult<J, R> successResult(Job<I, T> job, R result);
}
