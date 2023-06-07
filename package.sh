#!/bin/bash
packageDir=subscribeDemo-java

mvn clean package
if [[ -d $packageDir ]]; then
  rm -rf $packageDir
fi
mkdir $packageDir && \
mkdir $packageDir/config
cp target/$packageDir-0.0.1-SNAPSHOT.jar $packageDir && \
cp src/main/resources/* $packageDir/config && \
cp -R bin $packageDir && \
tar -cvf $packageDir-$(date "+%Y%m%d").tar $packageDir && \
rm -rf $packageDir