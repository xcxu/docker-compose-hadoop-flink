package com.xcxu.flinkhandle.service;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;

import static org.apache.flink.api.java.aggregation.Aggregations.SUM;
import static org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE;

public class LogAnalysis2 {
    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> accesslog = env.readTextFile("hdfs://localhost:9000/hadoop/data/access_log");
        accesslog.print();



    }


}
