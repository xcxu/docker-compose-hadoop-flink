# docker-compose-hadoop-flink
use docker-compose build hadoop &amp; flink  
hadoop version 2.8 reference images:bde2020/hadoop-base  
flink version 1.7.2  reference images:bde2020/flink-master:1.7.2-hadoop2.8  
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

## about flink dockerfile and some troubleshooting
1.debian默认apt源无法更新部分组件  
   COPY sources.list /etc/apt/sources.list  
   RUN mv /etc/apt/sources.list.d/jessie-backports.list /etc/apt/sources.list.d/jessie-backports.list.bak  

## docer-compose编排hadoop和flink中的一些问题
1.flink需要暴露8080，8081，6123端口，中途因为没有映射6123端口导致flinkUI无法看到TaskManger和JobManager关联起来   
2.flink中关联hadoop相关服务:  
	environment:  
     		SERVICE_PRECONDITION: "namenode:9870 datanode:9864"  



