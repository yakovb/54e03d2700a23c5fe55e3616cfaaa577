# What does this library do?
Think of `lib-microbatch` like a production line.
It provides abstractions for submitting jobs, processing them, and getting the result.
It does this by batching your submitted jobs into small (configurable) batches and processing those batches.

There is currently one microbatch implementation based on a queue and a timer.
Specifically, you submit jobs onto a queue and at every time slice, whatever is on the queue is processed.
So if you have many jobs hitting the queue (high throughput) a complete batch is processed at every tick.
But if you don't have many jobs hitting the queue (low throughput) you are guaranteed to process whatever you've got at every tick.

The library is generic in the sense that it can handle jobs and job results of any type.
However, it asks you to specify the types of jobs and job results. 
Why? 
Continuing with the production line metaphor: a good line is one that is specialised.
It handles one kind of workload and does it well. 
The batch processing this library supports operates on specific types of jobs, producing specific types of job results.
To have more variety in job and result types, simply create more production lines.  

# How do I use it?
There are four implementation requirements on you:
1. Implement a `Job`, by specifying an `id` and `payload` type;
2. Implement a `JobResult`, by specifying an `id` and `result` type;
3. Implement a `Batcher` specific to your `Job` and `JobResult`;
4. Implement a `BatchProcessor` that knows how to process on your `Job` and produce a `JobResult`.

There is also a configuration requirement.
You must supply your `Batcher` with some properties that govern its operation.
I recommend you store the properties in your standard properties file and read them as you do existing config.
The configurable options are:
1. `timeSliceMillis`, milliseconds to wait between processing jobs on the queue;
2. `waitTimeMillis`, milliseconds to wait before aborting a job submission;
3. `batchSize`, maximum batch size to process;
4. `queueCapacity`, maximum capacity of the underlying queue. 

The reason for setting a maximum queue capacity is to avoid an unbounded queue.
Without a such a bound there is a risk of consuming all available memory and the entire application being killed.
The better option is to notice many jobs failing to submit and either:
1. increase the queue capacity, or
2. increase the batch size, or
3. decrease the time slice, or
4. run more instances of the batcher. 

You can also change all of these parameters simultaneously. 

# Observability
## Counters
The `Batcher` exposes a `Counters` class which increments a set of counters when interesting operations occur.
Currently we track:
* number of submitted jobs,
* numer of processed jobs,
* number of failed jobs, i.e. those that did not process.

## Logging
The library uses SL4J as the logging framework, so it will pick up whatever logging implementation (e.g. Logback)

# Examples
You can find an example implementation in the `example` package.
The examples use:
* `Job<UUID, String>`, i.e `UUID` identifier and `String` job payload,
* `JobResult<Long, String>`, i.e. a `Long` identifier and a `String` job result. 
* An associated batcher for the pair above.

# Import with Maven
```xml
<dependency>
  <groupId>org.yakov</groupId>
  <artifactId>lib-microbatch</artifactId>
  <version>1.0-SNAPSHOT</version>
 </dependency>
``` 

# Testing
To get a sense of what the library does you should inspect the associated test class.
This is a property-based test which describes the expected behaviour of a `Batcher` in terms of its invariants. 

## Local testing
Requires Java 21 and Maven 3. 
Build the project with `mvn install`, which will also run the tests. 
To run the tests on their own, `mvn test`.

## Docker
I've included a Dockerfile to simplify a quick test run. 

To build the image, run `docker build -t lib-microbatch:latest .`

To execute the tests, run `docker run -it --rm lib-microbatch mvn test` 
