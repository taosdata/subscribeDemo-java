# subscribeDemo-java

## build

```shell
git clone https://github.com/taosdata/subscribeDemo-java.git
cd subscribeDemo-java
./package
```

## run

```shell
tar -xvf subscribeDemo-java-20230307.tar
cd subscribeDemo-java
bin/start.sh
```

## config
edit the `config/application.yml` and `consumer.properties` to change the configuration


## change log

### 2024-04-28
1. add parameter `subscriber.concurrency`: int, the number of threads to consume data, default is 1 #TD-4684

### 2023-08-07

1. deserializer parameter `deserializer.calculate.latency`: boolean, whether to calculate latency, default is 
   false#TS-3783

### 2023-07-13

1. deserializer parameter `deserializer.binary.as.string`: boolean, whether to output binary data as string, default is
   true

### 2023-07-07

1. parameter `record-formatter.with-partition-offset`: boolean, output 'dbName, topic, vGroupId, offset' when this
   parameter is true
2. parameter `subscriber.print-data-in-log`: boolean, print the data consumed in log when this parameter is true
3. parameter `subscriber.print-offset-in-log`: boolean, print the offset consumed in log when this parameter is true
4. parameter `subscriber.commit-after-poll`: boolean, commit the offset after poll when this parameter is true
5. parameter `subscriber.seek-to.offsets`: seek to the specified offset after consumer.subscribe() called
6. print all configurations on when log level is debug
7. fix: no test cases run, use junit5 instead of junit4
8. use fastjson-2.0.34 instead of fastjson-2.0.33
9. use taos-jdbcdriver-3.2.4 instead of taos-jdbcdriver-3.1.0
10. remove byte-buddy library

### 2023-06-06

1. Deprecated schema.txt, deprecated Class generation using bytebuddy library
2. parameter `record-formatter.type`: Support json, csv, kv (key value) three formats, output format, default is kv
3. parameter `record-formatter.csv.delimiter`: char, the csv separator, default is '\t'
4. parameter `record-formatter.csv.with-title`: boolean, whether to output a title, default is false
