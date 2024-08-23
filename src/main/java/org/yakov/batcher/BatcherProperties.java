package org.yakov.batcher;

public class BatcherProperties {
    private final long timeSliceMillis;
    private final long waitTimeMillis;
    private final int batchSize;
    private final int queueCapacity;

    public BatcherProperties(long timeSliceMillis, long waitTimeMillis, int batchSize, int queueCapacity) {
        this.timeSliceMillis = timeSliceMillis;
        this.waitTimeMillis = waitTimeMillis;
        this.batchSize = batchSize;
        this.queueCapacity = queueCapacity;
    }

    public long getTimeSliceMillis() {
        return timeSliceMillis;
    }

    public long getSubmitWaitTimeMillis() {
        return waitTimeMillis;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }
}
