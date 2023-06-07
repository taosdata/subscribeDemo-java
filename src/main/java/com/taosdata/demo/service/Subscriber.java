package com.taosdata.demo.service;

import com.taosdata.demo.deserializer.NewMapDeserializer;
import com.taosdata.demo.formatter.Formatter;
import com.taosdata.jdbc.tmq.ConsumerRecords;
import com.taosdata.jdbc.tmq.TMQConstants;
import com.taosdata.jdbc.tmq.TaosConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class Subscriber implements CommandLineRunner {

    @Value("${subscriber.topic-names}")
    private String[] topicNames;
    @Value("${subscriber.poll-timeout}")
    private int pollTimeout;
    @Value("${subscriber.output-file}")
    private String outputFile;
    @Value("${subscriber.consumer-properties-file}")
    private String consumerConfigFile;

    @Resource
    private Formatter formatter;

    private PrintWriter writer;

    @Override
    public void run(String... args) throws Exception {
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        } catch (Exception e) {
            throw new Exception("failed to create data file: " + outputFile + ", cause: " + e.getMessage(), e);
        }

        Properties properties;
        try {
            properties = checkProperties(consumerConfigFile);
        } catch (Exception e) {
            throw new Exception("failed to load properties: ,cause: " + e.getMessage(), e);
        }

        try {
            consume(properties);
        } catch (Exception e) {
            throw new Exception(
                    "failed to consume from topics: " + Arrays.toString(topicNames) + ", cause: " + e.getMessage(), e);
        }
    }

    private Properties checkProperties(String consumerConfig) throws IOException {
        log.info("consumer.properties path: " + consumerConfig);
        Properties properties = new Properties();
        properties.load(Files.newInputStream(new File(consumerConfig).toPath()));
        if (!properties.containsKey("group.id")) {
            properties.put("group.id", UUID.randomUUID().toString());
        }
        properties.put(TMQConstants.VALUE_DESERIALIZER, NewMapDeserializer.class.getName());
        log.info("consumer.properties: " + properties);
        return properties;
    }

    private void consume(Properties properties) throws Exception {
        try (TaosConsumer<Map<String, Object>> consumer = new TaosConsumer<>(properties)) {
            consumer.subscribe(Arrays.asList(topicNames));
            while (true) {
                try {
                    ConsumerRecords<Map<String, Object>> records = consumer.poll(Duration.ofMillis(pollTimeout));
                    for (Map<String, Object> record : records) {
                        writer.println(formatter.format(record));
                        writer.flush();
                    }
                } catch (SQLException e) {
                    throw new Exception("failed to poll from database cause: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
}