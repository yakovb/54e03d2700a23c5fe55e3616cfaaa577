package org.yakov.batcher;

/**
 * Holder of configuration options for a {@link Batcher}.
 */
public class BatcherProperties {
    private final long timeSliceMillis;
    private final long waitTimeMillis;
    private final int batchSize;
    private final int queueCapacity;

    /**
     *
     * @param timeSliceMillis milliseconds to wait between processing jobs on the queue
     * @param waitTimeMillis milliseconds to wait before aborting a job submission
     * @param batchSize maximum batch size to process
     * @param queueCapacity maximum capacity of the underlying queue
     */
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
