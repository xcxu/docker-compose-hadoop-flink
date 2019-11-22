# docker-compose-hadoop-flink
use docker-compose build hadoop &amp; flink

## start compose
docker-compose up -d

## hadoop namenode
localhost:50070

## hadoop resourcemanager
localhost:8088

## flink
localhost:8081

## flink batch demo
flink run flinkdataprocess-1.0-SNAPSHOT.jar

