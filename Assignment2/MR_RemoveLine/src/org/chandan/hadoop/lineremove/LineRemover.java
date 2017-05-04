package org.chandan.hadoop.lineremove;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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

public class LineRemover {

    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {

        @Override
        public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> outputCollector, Reporter reporter)
                throws IOException {
            if(value.getLength()>15) {
                outputCollector.collect(key, value);
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<LongWritable, Text, Text, NullWritable> {

        @Override
        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<Text, NullWritable> outputCollector,
                Reporter reporter) throws IOException {
            while(values.hasNext()) {
                outputCollector.collect(values.next(), NullWritable.get());
            }
        }

        
    }

    public static void main(String[] args) throws Exception{
        JobConf jobConf = new JobConf();
        jobConf.setJobName("Line Remover");
        jobConf.setJarByClass(LineRemover.class);
        jobConf.setMapperClass(MyMapper.class);
        //No need of reducer; Still used just to keep ordering of lines as same as input.
        jobConf.setReducerClass(MyReducer.class);
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setOutputKeyClass(LongWritable.class);
        jobConf.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
        JobClient.runJob(jobConf);
    }
}
