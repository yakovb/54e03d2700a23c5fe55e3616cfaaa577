package org.yakov.batcher;

import org.yakov.jobs.JobResult;

import java.util.Collection;

/**
 * Processor containing the logic to take a {@link org.yakov.jobs.Job} and produce a {@link JobResult}.
 *
 * @param <J> type of the {@link JobResult} identifier.
 * @param <R> type of {@link JobResult} payload.
 */
public interface BatchProcessor<J,R> {
	/**
	 * Process a collection of jobs which will result in either a result or an error.
	 *
	 * @param jobs The jobs to process.
	 */
	void process(Collection<JobResult<J,R>> jobs);
}