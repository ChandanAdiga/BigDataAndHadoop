package org.chandan.hadoop.mapsidejoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

    private static HashMap<String, String> cacheStoreLocation = new HashMap<String, String>();
    private Text output = new Text();

    @Override
    protected void setup(Context context)
            throws IOException, InterruptedException {
        BufferedReader bufferedReader = null;
        try {
            Path path = new Path("store_details");
            FileSystem fs = FileSystem.getLocal(context.getConfiguration());
            System.out.println("MyMapper > cache file path:" + path.toString());
            bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));
            String lineRead = "";
            while((lineRead = bufferedReader.readLine()) != null) {
                System.out.println("Read cache line:" + lineRead);
                String[] elements = lineRead.split(",");
                cacheStoreLocation.put(elements[0], elements[1] + "\t" + elements[2]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
        }
        System.out.println("MyMapper > Final cache size:" + cacheStoreLocation.size());
    }

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        try {
            String[] elements = value.toString().split(",");

            String cacheStoreLocationEntry = cacheStoreLocation.get(elements[0]);
            output.set(cacheStoreLocationEntry + "\t" + elements[1] + "\t" + elements[2]);
            System.out.println("Map side join o/p:" +output.toString());
            context.write(new Text(elements[0]/*Store id*/), output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}