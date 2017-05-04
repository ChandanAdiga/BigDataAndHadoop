package org.chandan.hadoop.counter;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
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
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class RecordCounter extends Configured implements Tool{

    private static final String COUNTER_GROUP_NAME = "RECORD_COUNTER";
    private static final String COUNTER_LESS_THAN_10 = "COUNTER_LESS_THAN_10";
    private static final String COUNTER_GREATER_THAN_50 = "COUNTER_GREATER_THAN_50";
    private static final String COUNTER_BETWEEN_10_50 = "COUNTER_BETWEEN_10_50";

    @Override
    public int run(String[] args) throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.setJobName("Record Counter");
        jobConf.setJarByClass(RecordCounter.class);
        jobConf.setMapperClass(MyMapper.class);
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setOutputKeyClass(Text.class);
        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));

        RunningJob runningJob = JobClient.runJob(jobConf);

        long counterLessThan10 = runningJob.getCounters().findCounter(COUNTER_GROUP_NAME, COUNTER_LESS_THAN_10).getCounter();
        long counterGreaterThan50 = runningJob.getCounters().findCounter(COUNTER_GROUP_NAME, COUNTER_GREATER_THAN_50).getCounter();
        long counterBetween20And50 = runningJob.getCounters().findCounter(COUNTER_GROUP_NAME, COUNTER_BETWEEN_10_50).getCounter();

        System.out.println("Records with value <=10 : " + counterLessThan10);
        System.out.println("Records with value >=50 : " + counterGreaterThan50);
        System.out.println("Records with 10< value <50 : " + counterBetween20And50);

        return 0;
    }

    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter)
                throws IOException {
            try {
                String[] elements = value.toString().split("\t");
                int score = Integer.parseInt(elements[1]);
                if(score <= 10) {
                    reporter.getCounter(COUNTER_GROUP_NAME,COUNTER_LESS_THAN_10).increment(1);
                } else if(score >= 50) {
                    reporter.getCounter(COUNTER_GROUP_NAME,COUNTER_GREATER_THAN_50).increment(1);
                } else {
                    reporter.getCounter(COUNTER_GROUP_NAME,COUNTER_BETWEEN_10_50).increment(1);
                }
                //No need to output..
                //outputCollector.collect(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int counterToolExitCode = ToolRunner.run(new RecordCounter(), args);
        System.exit(counterToolExitCode);
    }
}
