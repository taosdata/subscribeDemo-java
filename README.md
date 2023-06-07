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

## feature

```text
### 2023-06-06 refactor
1. Deprecated schema.txt, deprecated Class generation using bytebuddy library
2. parameter record-formatter.type: Support json, csv, kv (key value) three formats, output format, default is kv
3. parameter record-formatter.csv.delimiter: char, the csv separator, default is '\t'
4. parameter record-formatter.csv.with-title: boolean, whether to output a title, default is false
```

