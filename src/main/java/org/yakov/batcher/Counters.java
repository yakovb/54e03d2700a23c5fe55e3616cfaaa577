package org.yakov.batcher;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Container for useful information on the operations of a {@link Batcher}.
 */
public class Counters {
    private final AtomicReference<BigInteger> submitted;
    private final AtomicReference<BigInteger> failed;
    private final AtomicReference<BigInteger> processed;

    public Counters() {
        submitted = new AtomicReference<>(BigInteger.ZERO);
        failed = new AtomicReference<>(BigInteger.ZERO);
        processed = new AtomicReference<>(BigInteger.ZERO);
    }

    /**
     * @return number of jobs submitted.
     */
    public BigInteger getSubmitted() {
        return submitted.get();
    }

    /**
     * Increment submitted count.
     *
     * @param newlySubmitted increment number.
     * @return total submitted jobs, after the increment.
     */
    public BigInteger incrementSubmitted(BigInteger newlySubmitted) {
        return submitted.accumulateAndGet(newlySubmitted, BigInteger::add);
    }

    /**
     * @return number of failed jobs, i.e. submitted but not processed.
     */
    public BigInteger getFailed() {
        return failed.get();
    }

    /**
     * Increment failed count.
     *
     * @param newlyFailed increment number.
     * @return total failed jobs, after the increment.
     */
    public BigInteger incrementFailed(BigInteger newlyFailed) {
        return failed.accumulateAndGet(newlyFailed, BigInteger::add);
    }

    /**
     * @return number of processed jobs, i.e. submitted and processed.
     */
    public BigInteger getProcessed() {
        return processed.get();
    }

    /**
     * Increment processed count.
     *
     * @param newlyProcessed increment number.
     * @return total processed jobs, after the increment.
     */
    public BigInteger incrementProcessed(BigInteger newlyProcessed) {
        return processed.accumulateAndGet(newlyProcessed, BigInteger::add);
    }
}
