package com.taosdata.demo.formatter;

import java.util.Map;

public interface Formatter {

    String format(Map<String, Object> record);
}
