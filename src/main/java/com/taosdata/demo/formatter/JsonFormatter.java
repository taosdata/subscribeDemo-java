package com.taosdata.demo.formatter;

import com.alibaba.fastjson.JSONObject;
import com.taosdata.jdbc.tmq.ConsumerRecord;
import lombok.Builder;

import java.util.LinkedHashMap;
import java.util.Map;

@Builder
public class JsonFormatter implements Formatter {
    private boolean withPartitionOffset;

    @Override
    public String format(ConsumerRecord<Map<String, Object>> record) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (withPartitionOffset) {
            map.put("dbName", record.getDbName());
            map.put("topic", record.getTopic());
            map.put("vGroupId", record.getVGroupId());
            map.put("offset", record.getOffset());
        }
        map.putAll(record.value());
        return JSONObject.toJSONString(map);
    }
}
