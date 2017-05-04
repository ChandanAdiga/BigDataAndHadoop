package org.chandan.hadoop.linecount;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class LineCount {

    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

        private static final IntWritable one = new IntWritable(1);
        private Text lineCount = new Text("Total No Of Lines : ");

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter)
                throws IOException {
            outputCollector.collect(lineCount, one);
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable totalNoOfLines = new IntWritable(0);

        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> outputCollector,
                Reporter reporter) throws IOException {
            int lineCount = 0;
            while(values.hasNext()) {
                lineCount += values.next().get();
            }
            totalNoOfLines.set(lineCount);
            outputCollector.collect(key, totalNoOfLines);
        }
    }

    public static void main(String[] args) throws Exception{
        JobConf jobConf = new JobConf();
        jobConf.setJobName("Line Count");
        jobConf.setJarByClass(LineCount.class);
        jobConf.setMapperClass(MyMapper.class);
        jobConf.setReducerClass(MyReducer.class);
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(IntWritable.class);
        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
        JobClient.runJob(jobConf);
    }
}