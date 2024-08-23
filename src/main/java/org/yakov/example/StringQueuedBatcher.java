package org.yakov.example;

import org.yakov.batcher.BatchProcessor;
import org.yakov.batcher.BatcherProperties;
import org.yakov.batcher.Counters;
import org.yakov.batcher.QueuedBatcher;

import java.util.UUID;
import java.util.logging.Logger;

public class StringQueuedBatcher extends QueuedBatcher<UUID, Long, String, String> {

    public StringQueuedBatcher(BatchProcessor<Long, String> batchProcessor,
                               BatcherProperties props,
                               StringJobResultCreator stringJobResultCreator,
                               Counters counters) {
        super(batchProcessor, stringJobResultCreator, props, counters);
    }

    @Override
    protected Logger getLogger() {
        return null;
    }
}
