package org.chandan.hadoop.combine;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
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

public class Combiner {

    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, CountMaxMinWritable> {

        private final String TAB = "\t";
        private Text outputKey = new Text();
        private CountMaxMinWritable countMaxMinWritable = new CountMaxMinWritable();
        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, CountMaxMinWritable> outputCollector, Reporter reporter)
                throws IOException {
            try {
                String[] elements = value.toString().split(TAB);
                outputKey.set(elements[0]);
                countMaxMinWritable.setCount(1);
                float valueF = Float.parseFloat(elements[2]);
                countMaxMinWritable.setMax(valueF);
                countMaxMinWritable.setMin(valueF);
                outputCollector.collect(outputKey, countMaxMinWritable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, CountMaxMinWritable, Text, CountMaxMinWritable> {
        CountMaxMinWritable consolidatedRow = new CountMaxMinWritable();
        @Override
        public void reduce(Text key, Iterator<CountMaxMinWritable> values, OutputCollector<Text, CountMaxMinWritable> outputCollector,
                Reporter reporter) throws IOException {
            consolidatedRow.setCount(0);
            consolidatedRow.setMax(0);
            consolidatedRow.setMin(Integer.MAX_VALUE);
            
            int totalCount = 0;
            CountMaxMinWritable row;
            while(values.hasNext()) {
                row = values.next();
                totalCount += row.getCount();
                if(row.getMax() > consolidatedRow.getMax()) {
                    consolidatedRow.setMax(row.getMax());
                }
                if(row.getMin() < consolidatedRow.getMin()) {
                    consolidatedRow.setMin(row.getMin());
                }
            }
            consolidatedRow.setCount(totalCount);
            outputCollector.collect(key,consolidatedRow);
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.setJobName("Combiner");
        jobConf.setJarByClass(Combiner.class);
        jobConf.setMapperClass(MyMapper.class);
        jobConf.setCombinerClass(MyReducer.class);
        jobConf.setReducerClass(MyReducer.class);
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(CountMaxMinWritable.class);
        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
        JobClient.runJob(jobConf);
    }
}
