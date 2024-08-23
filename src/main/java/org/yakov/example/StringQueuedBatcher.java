package org.yakov.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yakov.batcher.BatchProcessor;
import org.yakov.batcher.BatcherProperties;
import org.yakov.batcher.Counters;
import org.yakov.batcher.QueuedBatcher;

import java.util.UUID;

public class StringQueuedBatcher extends QueuedBatcher<UUID, Long, String, String> {

    private static final Logger log = LoggerFactory.getLogger(StringQueuedBatcher.class);

    public StringQueuedBatcher(BatchProcessor<Long, String> batchProcessor,
                               BatcherProperties props,
                               StringJobResultCreator stringJobResultCreator,
                               Counters counters) {
        super(batchProcessor, stringJobResultCreator, props, counters);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
