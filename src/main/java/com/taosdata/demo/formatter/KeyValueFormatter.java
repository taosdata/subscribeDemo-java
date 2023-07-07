package com.taosdata.demo.formatter;

import com.taosdata.jdbc.tmq.ConsumerRecord;
import lombok.Builder;

import java.util.Map;

@Builder
public class KeyValueFormatter implements Formatter {

    private boolean withPartitionOffset;

    @Override
    public String format(ConsumerRecord<Map<String, Object>> record) {
        Map<String, Object> value = record.value();

        StringBuilder sb = new StringBuilder();
        if (withPartitionOffset) {
            sb.append("dbName:").append(record.getDbName()).append("\t");
            sb.append("topic:").append(record.getTopic()).append("\t");
            sb.append("vGroupId:").append(record.getVGroupId()).append("\t");
            sb.append("offset:").append(record.getOffset()).append("\t");
        }

        for (String key : value.keySet()) {
            sb.append(key).append(":").append(value.get(key)).append("\t");
        }
        return sb.toString();
    }
}
