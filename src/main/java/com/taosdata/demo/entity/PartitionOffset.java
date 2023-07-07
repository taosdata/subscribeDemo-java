package com.taosdata.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartitionOffset {
    private String topic;
    private int vGroupId;
    private long begin;
    private long offset;
    private long end;
}