package com.xcxu.flinkhandle.service;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import static org.apache.flink.api.java.aggregation.Aggregations.SUM;
import static org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE;

public class LogAnalysis {
    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> accesslog = env.readTextFile("hdfs://namenode:9000/hadoop/data/access_log");
        DataSet<Tuple5<String, String,String,String,String>> logData = accesslog.flatMap(new LineSplitter3());

        //输出数据
        logData.writeAsText("hdfs://namenode:9000/hadoop/basicdata/",OVERWRITE);
        //按url统计UV
        DataSet<Tuple2<String,Integer>> url_stat =
                logData.filter(new MyFilter())
                        .project(0,3)
                        .distinct()
                        .project(1)
                        .map(x->x.getField(0).toString())
                        .map(new MapCount())
                        .groupBy(0)
                        .aggregate(SUM,1)
                        .sortPartition(1, Order.DESCENDING);

        url_stat.writeAsText("hdfs://namenode:9000/hadoop/url_stat/",OVERWRITE);

        //按url统计PV
        DataSet<Tuple2<String,Integer>> url_stat_pv = logData.filter(new MyFilter())
                .project(0,3)
                .map(x->x.getField(1).toString())
                .map(new MapCount())
                .groupBy(0)
                .aggregate(SUM,1)
                .sortPartition(1, Order.DESCENDING)
                ;
        url_stat_pv.writeAsText("hdfs://namenode:9000/hadoop/url_stat_pv/",OVERWRITE);

        //按日期统计UV
        DataSet<Tuple2<String,Integer>> date_stat = logData.filter(new MyFilter())
                .map(new MapDateSub())
                .distinct()
                .map(x->x.getField(1).toString())
                .map(new MapCount())
                .groupBy(0)
                .aggregate(SUM,1)
                .sortPartition(1, Order.DESCENDING)
                ;

        date_stat.writeAsText("hdfs://namenode:9000/hadoop/date_stat/",OVERWRITE);

        //按日期统计UV
        DataSet<Tuple2<String,Integer>> date_stat_pv = logData.filter(new MyFilter())
                .map(new MapDateSub())
                .map(x->x.getField(1).toString())
                .map(new MapCount())
                .groupBy(0)
                .aggregate(SUM,1)
                .sortPartition(1, Order.DESCENDING)
                ;

        date_stat_pv.writeAsText("hdfs://namenode:9000/hadoop/date_stat_pv/",OVERWRITE);
        env.execute();

    }

    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {

            for (String word : line.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

    public static class MyFilter implements FilterFunction<Tuple5<String,String,String,String,String>> {

        @Override
        public boolean filter(Tuple5<String, String,String,String,String> value) throws Exception {
            return value.f3!="";
        }
    }

    public static class LineSplitter3 implements FlatMapFunction<String, Tuple5<String, String,String,String,String>> {

    //返回：ip+时间+请求方式+url+status

        @Override
        public void flatMap(String line, Collector<Tuple5<String, String,String,String,String>> out) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


            String[] words = line.split(" ");
            if(words.length>10) {
                String request_date;
                try {
                    request_date = sdf2.format(sdf.parse((words[3]+" " + words[4]).replace("[", "").replace("]", "")));
                } catch (Exception e) {
                    request_date = "";
                }
                out.collect(new Tuple5<String, String, String, String, String>(words[0]
                        , request_date
                        , words[5].replace("\"","")
                        , words[6].split("\\?")[0]
                        , words[8]));
            }
        }
    }


    public static class MapCount implements MapFunction<String,Tuple2<String,Integer>> {

        @Override
        public Tuple2<String,Integer> map(String input){
            return new Tuple2<String,Integer>(input,1);
        }
    }


    public static class MapDateSub implements MapFunction<Tuple5<String,String,String,String,String>,Tuple2<String,String>> {

        @Override
        public Tuple2<String,String> map(Tuple5<String,String,String,String,String> input){
            return new Tuple2<String,String>(input.getField(0),input.getField(1).toString().split(" ")[0]);
        }
    }




    public static class LineSplitter2 implements FlatMapFunction<String, String> {
        @Override
        public void flatMap(String line, Collector<String> out) {
            StringBuffer sbf = new StringBuffer();
            int len = line.split(" ").length;
            if(line.split(" ").length<10) {
                for (String word : line.split(" ")) {
//                out.collect(word);
                    sbf.append(word).append("|||||");

                }
            }
            out.collect("size is "+len+":"+sbf.toString());
        }
    }
}
