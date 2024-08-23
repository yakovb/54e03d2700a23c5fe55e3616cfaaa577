package org.yakov.jobs;

public interface Job<I, T> {
	I getId();
	T getPayload();
}