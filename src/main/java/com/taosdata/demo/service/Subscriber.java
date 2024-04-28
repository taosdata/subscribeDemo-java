package com.taosdata.demo.service;

import com.taosdata.demo.entity.PartitionOffset;
import com.taosdata.demo.entity.SeekToPartitionOffsets;
import com.taosdata.demo.formatter.Formatter;
import com.taosdata.demo.util.ConsumerPropertyLoader;
import com.taosdata.jdbc.tmq.ConsumerRecord;
import com.taosdata.jdbc.tmq.ConsumerRecords;
import com.taosdata.jdbc.tmq.TaosConsumer;
import com.taosdata.jdbc.tmq.TopicPartition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class Subscriber implements CommandLineRunner {

    @Value("${subscriber.consumer-properties-file}")
    private String consumerConfigFile;
    @Value("${subscriber.topic-names}")
    private String[] topicNames;
    @Value("${subscriber.poll-timeout}")
    private int pollTimeout;
    @Value("${subscriber.print-data-in-log: false}")
    private boolean printDataInLog;
    @Value("${subscriber.print-offset-in-log: false}")
    private boolean printOffsetInLog;
    @Value("${subscriber.commit-after-poll}")
    private boolean commitAfterPoll;
    @Value("${subscriber.concurrency}")
    private int concurrency;

    @Resource
    private Formatter formatter;
    @Resource
    private SeekToPartitionOffsets seekToPartitionOffsets;
    @Resource
    private PrintWriter writer;

    //private TaosConsumer<Map<String, Object>> consumer;

    @Override
    public void run(String... args) throws Exception {
        Properties properties;
        try {
            log.info("consumer.properties path: {}", consumerConfigFile);
            properties = ConsumerPropertyLoader.load(consumerConfigFile);
            log.info("consumer.properties: {}", properties);
        } catch (Exception e) {
            throw new Exception("failed to load properties: ,cause: " + e.getMessage(), e);
        }

        List<Thread> consumerList = IntStream.range(0, concurrency).mapToObj(i -> {
            Thread t = new Thread(() -> {
                log.info("{} start to consume topics: {}", Thread.currentThread().getName(),
                        Arrays.toString(topicNames));
                try {
                    consume(properties);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "failed to consume topics: " + Arrays.toString(topicNames) + ", cause: " + e.getMessage(),
                            e);
                }
                log.info("{} stop to consume topics: {}", Thread.currentThread().getName(),
                        Arrays.toString(topicNames));
            });
            return t;
        }).collect(Collectors.toList());

        consumerList.forEach(Thread::start);

        for (Thread t : consumerList) {
            t.join();
        }
    }

    private void consume(Properties properties) throws Exception {
        TaosConsumer<Map<String, Object>> consumer = null;
        try {
            consumer = new TaosConsumer<>(properties);

            consumer.subscribe(Arrays.asList(topicNames));
            log.info(offsetsInfo(consumer, "subscription created"));

            List<PartitionOffset> offsets = seekToPartitionOffsets.getOffsets();
            if (offsets != null && !offsets.isEmpty()) {
                seekTo(consumer, seekToPartitionOffsets.getOffsets());
                printOffsets(consumer, "after seek");
            }

            int count = 0;
            while (true) {
                printOffsets(consumer, "before poll(" + count + ")");
                try {
                    ConsumerRecords<Map<String, Object>> records = consumer.poll(Duration.ofMillis(pollTimeout));
                    for (ConsumerRecord<Map<String, Object>> record : records) {
                        String line = formatter.format(record);
                        if (printDataInLog) {
                            log.info(line);
                        }
                        writer.println(line);
                        writer.flush();
                    }
                } catch (SQLException e) {
                    throw new Exception("failed to poll from database cause: " + e.getMessage(), e);
                }
                printOffsets(consumer, "after poll(" + count + ")");

                if (commitAfterPoll) {
                    consumer.commitSync();
                    printOffsets(consumer, "after commit(" + count + ")");
                }

                count++;
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (consumer != null)
                consumer.close();
        }
    }

    private void seekTo(TaosConsumer<Map<String, Object>> consumer, List<PartitionOffset> offsets) throws Exception {
        offsets = offsets.stream().filter(i -> {
            boolean contains = Arrays.asList(topicNames).contains(i.getTopic());
            if (!contains) {
                log.warn("topic: {} not in topicNames: {}", i.getTopic(), Arrays.toString(topicNames));
            }
            return contains;
        }).collect(Collectors.toList());

        for (PartitionOffset offset : offsets) {
            log.info("seek to: {}", offset);
            try {
                consumer.seek(new TopicPartition(offset.getTopic(), offset.getVGroupId()), offset.getOffset());
            } catch (SQLException e) {
                throw new Exception("failed to seek to: {" + offset + "}", e);
            }
        }
    }

    private void printOffsets(TaosConsumer<Map<String, Object>> consumer, String message) throws SQLException {
        if (!printOffsetInLog)
            return;
        log.info(offsetsInfo(consumer, message));
    }

    private String offsetsInfo(TaosConsumer<Map<String, Object>> consumer, String message) throws SQLException {
        StringBuilder sb = new StringBuilder(message + " => ");
        for (String topic : topicNames) {
            Map<Integer, Long> begin = consumer.beginningOffsets(topic)
                                               .entrySet()
                                               .stream()
                                               .collect(Collectors.toMap(e -> e.getKey().getVGroupId(),
                                                       Map.Entry::getValue));

            Map<Integer, Long> end = consumer.endOffsets(topic)
                                             .entrySet()
                                             .stream()
                                             .collect(Collectors.toMap(e -> e.getKey().getVGroupId(),
                                                     Map.Entry::getValue));

            Map<Integer, Long> current = consumer.position(topic)
                                                 .entrySet()
                                                 .stream()
                                                 .collect(Collectors.toMap(e -> e.getKey().getVGroupId(),
                                                         Map.Entry::getValue));

            sb.append(String.format("{topic: %s, begin: %s, end: %s, current: %s} ", topic, begin, end, current));
        }
        return sb.toString();
    }

}