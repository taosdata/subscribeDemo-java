package com.taosdata.demo.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecordMap {
    public static Map<String, Object> m = new LinkedHashMap<>();

    static {
        m.put("ts", 123L);
        m.put("f1", (byte) 1);
        m.put("f2", (short) 2);
        m.put("f3", 3);
        m.put("f4", (long) 4);
        m.put("f5", 5.55f);
        m.put("f6", 6.6666d);
        m.put("f7", Byte.valueOf("7"));
        m.put("f8", Short.valueOf("8"));
        m.put("f9", Integer.valueOf("9"));
        m.put("f10", Long.valueOf("10"));
        m.put("f11", Float.valueOf("11.11"));
        m.put("f12", Double.valueOf("12.2222"));
        m.put("f13", "abc");
        m.put("f14", "北京");
    }

}
