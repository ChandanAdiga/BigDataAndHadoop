package org.chandan.hadoop.prj.cars.frequency200hp;

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
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MRCarsModel75Details {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private Text outputKey = new Text();
        private IntWritable outputValue = new IntWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                System.out.println("MyMapper: " + value.toString());
                String[] attributes = value.toString().split(";");
                if("75".equals(attributes[7])) {
                    if("US".equalsIgnoreCase(attributes[8]) || "Europe".equalsIgnoreCase(attributes[8]) || "Japan".equalsIgnoreCase(attributes[8])) {
                        outputKey.set(attributes[8]);
                        outputValue.set(1);
                        System.out.println("MyMapper - Writing <" + outputKey.toString() +"," + outputValue.toString() + ">");
                        context.write(outputKey, outputValue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyReducer extends Reducer<Text, IntWritable, Text, Text> {
        private Text outputValue = new Text();
        private int maxNoOfProductions = 0;
        private String originOfMaxProduction;
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int frequency = 0;
            for(IntWritable entry:values) {
                frequency += entry.get();
            }
            if(maxNoOfProductions < frequency) {
                maxNoOfProductions = frequency;
                originOfMaxProduction = key.toString();
            }
            outputValue.set("Frequency :" + frequency);
            System.out.println("MyReducer: <" + key.toString() +"," + outputValue.toString() + ">");
            context.write(key, outputValue);
        }

        @Override
        protected void cleanup(Reducer<Text, IntWritable, Text, Text>.Context context)
                throws IOException, InterruptedException {
            System.out.println("MyReducer: Max production <" + originOfMaxProduction +"," + maxNoOfProductions + ">");
            context.write(new Text("Maximum Cars Produced Country:" + originOfMaxProduction), new Text("Manufactured cars:" + maxNoOfProductions));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"MRCarsModel75Details");
        job.setJarByClass(MRCarsModel75Details.class);

        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
