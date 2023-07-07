package com.taosdata.demo.util;

import com.taosdata.demo.deserializer.NewMapDeserializer;
import com.taosdata.jdbc.tmq.TMQConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

@Slf4j
public class ConsumerPropertyLoader {

    public static Properties load(String consumerConfig) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(new File(consumerConfig).toPath()));
        if (!properties.containsKey("group.id")) {
            properties.put("group.id", UUID.randomUUID().toString());
        }
        properties.put(TMQConstants.VALUE_DESERIALIZER, NewMapDeserializer.class.getName());
        return properties;
    }
}
