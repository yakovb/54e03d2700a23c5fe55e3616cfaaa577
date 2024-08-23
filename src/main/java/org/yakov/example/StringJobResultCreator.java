package org.yakov.example;

import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;
import org.yakov.jobs.JobResultCreator;

import java.util.Random;
import java.util.UUID;

public class StringJobResultCreator implements JobResultCreator<UUID, Long, String, String> {
    private final Random random = new Random();

    @Override
    public JobResult<Long, String> shutdownResult(Job<UUID, String> job) {
        var result = new StringJobResult(random.nextLong(), job);
        result.populateError(JobResult.Error.SUBMIT_FAILED);
        return result;
    }

    @Override
    public JobResult<Long, String> pendingResult(Job<UUID, String> job) {
        return new StringJobResult(random.nextLong(), job);
    }

    @Override
    public JobResult<Long, String> errorResult(Job<UUID, String> job, JobResult.Error error) {
        var result = new StringJobResult(random.nextLong(), job);
        result.populateError(error);
        return result;
    }

    @Override
    public JobResult<Long, String> successResult(Job<UUID, String> job, String successPayload) {
        var result = new StringJobResult(random.nextLong(), job);
        result.populateResult(successPayload);
        return result;
    }

}
