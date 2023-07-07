package com.taosdata.demo.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerPropertyLoaderTest {

    @Test
    void load() throws IOException {
        // when
        Properties p = ConsumerPropertyLoader.load("src/main/resources/consumer.properties");
        // then
        assertEquals("taos-demo", p.getProperty("group.id"));
    }
}