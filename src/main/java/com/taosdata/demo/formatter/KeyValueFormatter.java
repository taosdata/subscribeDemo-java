package com.taosdata.demo.formatter;

import java.util.Map;

public class KeyValueFormatter implements Formatter {

    @Override
    public String format(Map<String, Object> record) {
        StringBuilder sb = new StringBuilder();
        for (String key : record.keySet()) {
            sb.append(key).append(":").append(record.get(key)).append("\t");
        }
        return sb.toString();
    }
}
