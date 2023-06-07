package com.taosdata.demo.formatter;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class JsonFormatter implements Formatter {
    @Override
    public String format(Map<String, Object> record) {
        return JSONObject.toJSONString(record);
    }
}
