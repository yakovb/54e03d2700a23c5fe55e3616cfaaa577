package org.yakov.batcher;

import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;

public interface Batcher<I, J, T, R> {
    JobResult<J, R> submit(Job<I, T> job);

    void shutdown();

    boolean isShutdown();

    Counters getCounters();
}