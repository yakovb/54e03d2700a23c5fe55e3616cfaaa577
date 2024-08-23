package org.yakov.batcher;

import org.yakov.jobs.JobResult;

import java.util.Collection;

public interface BatchProcessor<T,R> {
	// TODO should get the job from the result, populate the result
	// consider running batches in a separate thread, or wrapping this class in order to do so (one for the readme, not javadocs)
	void process(Collection<JobResult<T,R>> jobs);
}