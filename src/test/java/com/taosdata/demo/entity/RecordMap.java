package com.taosdata.demo.entity;

import com.taosdata.jdbc.tmq.ConsumerRecord;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecordMap {
    public static Map<String, Object> value = new LinkedHashMap<>();
    static {
        value.put("ts", 123L);
        value.put("f1", (byte) 1);
        value.put("f2", (short) 2);
        value.put("f3", 3);
        value.put("f4", (long) 4);
        value.put("f5", 5.55f);
        value.put("f6", 6.6666d);
        value.put("f7", Byte.valueOf("7"));
        value.put("f8", Short.valueOf("8"));
        value.put("f9", Integer.valueOf("9"));
        value.put("f10", Long.valueOf("10"));
        value.put("f11", Float.valueOf("11.11"));
        value.put("f12", Double.valueOf("12.2222"));
        value.put("f13", "abc");
        value.put("f14", "北京");
    }

    public static ConsumerRecord<Map<String, Object>> m = new ConsumerRecord<>("topicName", "dbName", 1, 1, value);
}
