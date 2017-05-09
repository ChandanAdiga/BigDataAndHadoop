package org.chandan.hadoop.prj.cars.weight2500;

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

public class MRCarsWeight2500 {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private Text outputKey = new Text();
        private IntWritable outputValue = new IntWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                System.out.println("MyMapper: " + value.toString());
                String[] attributes = value.toString().split(";");
                Float weight = Float.parseFloat(attributes[5]);
                if(weight < 2500f) {
                    if("6".equalsIgnoreCase(attributes[2]) || "4".equalsIgnoreCase(attributes[2]) || "3".equalsIgnoreCase(attributes[2])) {
                        outputKey.set(attributes[2]);
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
        private Text outputKey = new Text();
        private Text outputValue = new Text();
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int frequency = 0;
            for(IntWritable entry:values) {
                frequency += entry.get();
            }
            outputKey.set("" + frequency + " cars with");
            outputValue.set(key.toString() + " cylinders.");
            System.out.println("MyReducer: <" + key.toString() + outputValue.toString() + ">");
            context.write(outputKey, outputValue);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"MRCarsWeight2500");
        job.setJarByClass(MRCarsWeight2500.class);

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
