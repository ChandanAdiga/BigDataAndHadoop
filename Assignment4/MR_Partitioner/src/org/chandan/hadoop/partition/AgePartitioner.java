package org.chandan.hadoop.partition;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AgePartitioner {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text output = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String[] elements = value.toString().split("\t");
                output.set(elements[2]);//Map by Gender
                context.write(output, value);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyPartitioner extends Partitioner<Text, Text> {

        @Override
        public int getPartition(Text key, Text value, int numReduceTasks) {
            if(numReduceTasks < 3) {
                return 0;
            }

            String[] elements = value.toString().split("\t");
            int age = Integer.parseInt(elements[1]);

            if(age <= 20) {
                return 0;
            }
            if(age <= 50) {
                return 1;
            }
            return 2;
        }
    }

    public static class MyReducer extends Reducer<Text, Text, Text, Text> {
        private Text output = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                        throws IOException, InterruptedException {
            int maxScore = 0;
            for (Text text : values) {
                String[] elements = text.toString().split("\t");
                int score = Integer.parseInt(elements[3]);
                if(score > maxScore) {
                    maxScore = score;
                    output.set(text);
                }
            }
            context.write(key, output);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"Partitioner");
        job.setJarByClass(AgePartitioner.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        job.setPartitionerClass(MyPartitioner.class);
        job.setNumReduceTasks(3);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
