package org.yakov.batcher;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Counters {
    private final AtomicReference<BigInteger> submitted;
    private final AtomicReference<BigInteger> failed;
    private final AtomicReference<BigInteger> processed;

    public Counters() {
        submitted = new AtomicReference<>(BigInteger.ZERO);
        failed = new AtomicReference<>(BigInteger.ZERO);
        processed = new AtomicReference<>(BigInteger.ZERO);
    }

    public BigInteger getSubmitted() {
        return submitted.get();
    }

    public BigInteger incrementSubmitted(BigInteger newlySubmitted) {
        return submitted.accumulateAndGet(newlySubmitted, BigInteger::add);
    }

    public BigInteger getFailed() {
        return failed.get();
    }

    public BigInteger incrementFailed(BigInteger newlyRejected) {
        return failed.accumulateAndGet(newlyRejected, BigInteger::add);
    }

    public BigInteger getProcessed() {
        return processed.get();
    }

    public BigInteger incrementProcessed(BigInteger newlyProcessed) {
        return processed.accumulateAndGet(newlyProcessed, BigInteger::add);
    }
}
