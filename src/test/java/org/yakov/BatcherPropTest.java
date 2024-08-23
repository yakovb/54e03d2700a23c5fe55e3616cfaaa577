package org.yakov;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.yakov.batcher.Batcher;
import org.yakov.batcher.BatcherProperties;
import org.yakov.batcher.Counters;
import org.yakov.example.StringJob;
import org.yakov.example.StringJobResultCreator;
import org.yakov.example.StringQueuedBatcher;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Label("Verify batcher behaviour")
public class BatcherPropTest {

    @Label("Single simple job")
    @Property(tries = 10)
    void oneJob() {
        var batcher = batcher();
        batcher.submit(new StringJob(UUID.randomUUID(), "good"));

        await().atMost(1, TimeUnit.SECONDS).until(() -> {
                    var counters = batcher.getCounters();
                    var submitted = counters.getSubmitted().intValue();
                    var processed = counters.getProcessed().intValue();
                    var failed = counters.getFailed().intValue();

                    var correctSplit = submitted == 1 && processed == 1 && failed == 0;
                    var correctTotal = processed + failed == 1;
                    return correctSplit && correctTotal;
                }
        );
    }

    @Label("Submitted jobs all processed")
    @Property(tries = 10)
    void manyJobs(
            @ForAll @IntRange(min = 10, max = 100) int jobCount
    ) {
        var batcher = batcher();

        for (int i = 0; i < jobCount; i++) {
            batcher.submit(new StringJob(UUID.randomUUID(), "good" + i));
        }

        await().atMost(1, TimeUnit.SECONDS).until(() -> {
                    var counters = batcher.getCounters();
                    var submitted = counters.getSubmitted().intValue();
                    var processed = counters.getProcessed().intValue();
                    var failed = counters.getFailed().intValue();

                    var correctSplit = submitted == jobCount && processed == jobCount && failed == 0;
                    var correctTotal = processed + failed == jobCount;
                    return correctSplit && correctTotal;
                }
        );
    }

    @Label("Shutdown batcher rejects all jobs")
    @Property(tries = 10)
    void noJobs(
            @ForAll @IntRange(min = 10, max = 100) int jobCount
    ) {
        var batcher = batcher();
        batcher.shutdown();

        for (int i = 0; i < jobCount; i++) {
            batcher.submit(new StringJob(UUID.randomUUID(), "bad" + i));
        }

        await().atMost(1, TimeUnit.SECONDS).until(() -> {
                    var counters = batcher.getCounters();
                    var submitted = counters.getSubmitted().intValue();
                    var processed = counters.getProcessed().intValue();
                    var failed = counters.getFailed().intValue();

                    var correctSplit = submitted == jobCount && processed == 0 && failed == jobCount;
                    var correctTotal = processed + failed == jobCount;
                    return correctSplit && correctTotal;
                }
        );
    }

    @Label("Processes mixture of valid and invalid jobs")
    @Property(tries = 5)
    void mixedJobs(
            @ForAll @IntRange(min = 10, max = 100) int goodCount,
            @ForAll @IntRange(min = 10, max = 100) int badCount
    ) throws InterruptedException {
        var batcher = batcher();

        // Submit jobs that should complete
        for (int i = 0; i < goodCount; i++) {
            batcher.submit(new StringJob(UUID.randomUUID(), "good" + i));
        }

        // Submit jobs that should fail
        Thread.sleep(500);
        batcher.shutdown();
        for (int i = 0; i < badCount; i++) {
            batcher.submit(new StringJob(UUID.randomUUID(), "bad" + i));
        }

        await().atMost(1, TimeUnit.SECONDS).until(() -> {
                    var counters = batcher.getCounters();
                    var submitted = counters.getSubmitted().intValue();
                    var processed = counters.getProcessed().intValue();
                    var failed = counters.getFailed().intValue();

                    var okSubmitted = (goodCount + badCount) == submitted;
                    var okProcessed = goodCount == processed;
                    var okFailed = badCount == failed;
                    var correctTotal = processed + failed == goodCount + badCount;

                    return okSubmitted && okProcessed & okFailed && correctTotal;
                }
        );
    }

    Batcher<UUID, Long, String, String> batcher() {
        return new StringQueuedBatcher(
                jobs -> {
                },
                new BatcherProperties(1, 100, 10, 1000),
                new StringJobResultCreator(),
                new Counters());
    }
}
