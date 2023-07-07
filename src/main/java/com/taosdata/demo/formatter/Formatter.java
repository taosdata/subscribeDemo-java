package com.taosdata.demo.formatter;

import com.taosdata.jdbc.tmq.ConsumerRecord;

import java.util.Map;

public interface Formatter {

    String format(ConsumerRecord<Map<String, Object>> record);
}
