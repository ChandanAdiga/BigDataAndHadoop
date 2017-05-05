package org.chandan.hadoop.equijoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapperStoreLocation extends Mapper<LongWritable, Text, Text, Text> {
    private Text output = new Text();
    private static final String IDENTIFIER = "LOC";
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        try {
            String[] elements = value.toString().split(",");
            output.set(IDENTIFIER +"," + elements[1] + "," + elements[2]);
            System.out.println("Location o/p:" +output.toString());
            context.write(new Text(elements[0]/*Store id*/), output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
