package com.taosdata.demo;

import com.taosdata.jdbc.tmq.ConsumerRecords;
import com.taosdata.jdbc.tmq.TaosConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class Subscriber implements CommandLineRunner {

    @Value("${topicName}")
    private String topicName;
    @Value("${pollingInterval}")
    private int pollingInterval;
    @Value("${dataFile}")
    private String dataFile;
    @Value("${consumerProperties}")
    private String consumerConfig;
    @Value("${schema}")
    private String schemaTxt;

    private PrintWriter writer;
    private Class dynamicRecordClass;
    private Class dynamicRecordDeserializerClass;
    private Map<String, Integer> fieldIndex = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        try {
            writer = createWriter(dataFile);
        } catch (Exception e) {
            throw new Exception("failed to create data file: " + dataFile + ", cause: " + e.getMessage(), e);
        }

        try {
            String schema = new String(Files.readAllBytes(Paths.get(schemaTxt)));
            DynamicRecordUtil util = new DynamicRecordUtil();
            dynamicRecordClass = util.getDynamicRecordClass(schema);
            dynamicRecordDeserializerClass = util.getDynamicRecordDeserializer(dynamicRecordClass);
            buildFieldIndex(dynamicRecordClass);
        } catch (Exception e) {
            throw new Exception(
                    "failed to create dynamicRecord class and deserializer with schema: " + schemaTxt + ", cause: " +
                            e.getMessage(), e);
        }

        Properties properties;
        try {
            properties = checkProperties(consumerConfig);
        } catch (Exception e) {
            throw new Exception("failed to load properties: ,cause: " + e.getMessage(), e);
        }

        try {
            consume(properties);
        } catch (Exception e) {
            throw new Exception("failed to consume from topic: " + topicName + ", cause: " + e.getMessage(), e);
        }
    }

    private void buildFieldIndex(Class dynamicRecordClass) {
        Field[] fields = dynamicRecordClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fieldIndex.put(fields[i].getName(), i);
        }
    }

    private Properties checkProperties(String consumerConfig) throws IOException {
        log.info("consumer.properties path: " + consumerConfig);
        Properties properties = new Properties();
        properties.load(Files.newInputStream(new File(consumerConfig).toPath()));
        if (!properties.containsKey("value.deserializer")) {
            properties.put("value.deserializer", dynamicRecordDeserializerClass.getName());
        }
        if (!properties.containsKey("group.id")) {
            properties.put("group.id", UUID.randomUUID().toString());
        }
        log.info("consumer.properties: " + properties);
        return properties;
    }

    private PrintWriter createWriter(String filePath) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
    }

    private void consume(Properties properties) throws Exception {
        try (TaosConsumer<Object> consumer = new TaosConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(topicName));
            writer.println(title());
            while (true) {
                try {
                    ConsumerRecords<Object> records = consumer.poll(Duration.ofMillis(pollingInterval));
                    for (Object record : records) {
                        writer.println(format(record));
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

    private String title() {
        String title = "";
        Field[] fields = dynamicRecordClass.getDeclaredFields();
        for (Field field : fields) {
            title += field.getName() + "\t";
        }
        return title;
    }

    private String format(Object record) throws Exception {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(dynamicRecordClass);
        } catch (IntrospectionException e) {
            throw new Exception(
                    "failed to handle dynamicRecord class: " + dynamicRecordClass.getTypeName() + ", record: " +
                            record + ", " + "cause:" + " " + e.getMessage(), e);
        }

        Field[] fields = dynamicRecordClass.getDeclaredFields();

        String[] line = new String[fields.length];
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            String fieldName = descriptor.getName();
            int index = getIndex(fieldName);
            if (index == -1)
                continue;
            Method readMethod = descriptor.getReadMethod();
            Object value = readMethod.invoke(record);
            line[index] = value == null ? "NULL" : value.toString();
        }
        return String.join("\t", line);
    }

    private int getIndex(String fieldName) {
        if (fieldIndex.containsKey(fieldName))
            return fieldIndex.get(fieldName);
        return -1;
    }
}