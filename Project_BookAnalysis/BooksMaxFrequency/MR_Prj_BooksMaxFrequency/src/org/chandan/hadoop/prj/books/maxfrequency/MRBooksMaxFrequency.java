package org.chandan.hadoop.prj.books.maxfrequency;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MRBooksMaxFrequency {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private Text outputKey = new Text();
        private IntWritable outputValue = new IntWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                final String[] attributes = value.toString().split(";");
                final String year = attributes[3].replaceAll("\"", "");
                if(year.length() == 4 && year.matches("[0-9]*")) {
                    outputKey.set(year);
                    outputValue.set(1);
                    System.out.print(" Mapping - year:"+year);
                    context.write(outputKey, outputValue);
                } else {
                    System.out.print(" Not Mapping - year:"+year);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyReducer extends Reducer<Text, IntWritable, Text, Text> {
        private Text outputKey = new Text();
        private Text outputValue = new Text();
        private int mMaxFrequency;
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int booksCount = 0;
            for(IntWritable entry:values) {
                booksCount += entry.get();
            }
            System.out.println("Reducing for year " + key + " [Current max:" + mMaxFrequency +"] : - booksCount:"+booksCount);
            if(mMaxFrequency < booksCount) {
                System.out.println("Reduce - Updating maxFrquency to :"+booksCount);
                mMaxFrequency = booksCount;
                outputKey.set("Maximum boooks published:" + mMaxFrequency);
                outputValue.set("Year:" + key.toString());
            }
        }
        
        @Override
        protected void cleanup(Context context)
                throws IOException, InterruptedException {
            context.write(outputKey, outputValue);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"MRBooksMaxFrequency");
        job.setJarByClass(MRBooksMaxFrequency.class);

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(MyReducer.class);

        job.setOutputKeyClass(TextInputFormat.class);
        job.setOutputValueClass(TextOutputFormat.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
