package org.yakov.jobs;

/**
 * The primitive representing a job.
 *
 * @param <I> type of the job's identifier.
 * @param <T> type of the job's payload.
 */
public interface Job<I, T> {
	I getId();
	T getPayload();
}