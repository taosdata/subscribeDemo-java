package com.taosdata.demo;

import com.taosdata.jdbc.tmq.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class NewMapDeserializerTest {

    private static final String bootstrapServers = "192.168.1.92:6041";
    private static final String topicName = "test_sub";

    public static void main(String[] args) {
        Properties properties = new Properties();
        // tdengine configuration
        properties.put(TMQConstants.BOOTSTRAP_SERVERS, bootstrapServers);
        properties.put(TMQConstants.CONNECT_USER, "root");
        properties.put(TMQConstants.CONNECT_PASS, "taosdata");
        properties.put(TMQConstants.CONNECT_TYPE, "ws");
        properties.put(TMQConstants.GROUP_ID, UUID.randomUUID().toString());
        // !!! NOTICE: use NewMapDeserializer instead of default MapDeserializer
        properties.put(TMQConstants.VALUE_DESERIALIZER, NewMapDeserializer.class.getName());

        // custom configuration
        properties.put(NewMapDeserializer.TIMESTAMP_FORMAT, "yyyy-MM-dd HH:mm:ss.SSS");

        try (TaosConsumer<Map<String, Object>> consumer = new TaosConsumer<>(properties)) {
            consumer.subscribe(Collections.singleton(topicName));

            while (true) {
                ConsumerRecords<Map<String, Object>> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    continue;
                }
                for (Map<String, Object> record : records) {
                    printRecord(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printRecord(Map<String, Object> record) {
        for (String key : record.keySet()) {
            System.out.printf("%s:%s\t", key, record.get(key));
        }
        System.out.println();
    }

    private static class NewMapDeserializer implements Deserializer<Map<String, Object>> {

        public static final String TIMESTAMP_FORMAT = "timestamp.format";

        private String timestampFormat = "yyyy-MM-dd HH:mm:ss";

        @Override
        public void configure(Map<?, ?> configs) {
            if (configs.containsKey(TIMESTAMP_FORMAT)) {
                timestampFormat = (String) configs.get(TIMESTAMP_FORMAT);
            }
        }

        @Override
        public Map<String, Object> deserialize(ResultSet data) throws DeserializerException, SQLException {
            Map<String, Object> map = new LinkedHashMap<>();

            ResultSetMetaData metaData = data.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String colName = metaData.getColumnLabel(i);
                int colType = metaData.getColumnType(i);

                Object value;
                if (Types.TIMESTAMP == colType) {
                    value = format(data.getLong(colName));
                } else if (Types.BINARY == colType || Types.VARCHAR == colType || Types.NCHAR == colType) {
                    value = data.getString(colName);
                } else {
                    value = data.getObject(colName);
                }
                map.put(colName, value);
            }

            return map;
        }

        private String format(long ts) {
            SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
            return sdf.format(ts);
        }
    }
}