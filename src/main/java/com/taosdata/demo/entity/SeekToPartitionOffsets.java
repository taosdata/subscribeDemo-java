package com.taosdata.demo.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "subscriber.seek-to")
public class SeekToPartitionOffsets {
    List<PartitionOffset> offsets;
}