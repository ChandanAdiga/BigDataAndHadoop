package org.chandan.hadoop.equijoin;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducerStoreLocationAndSales extends Reducer<Text, Text, Text, Text> {

    private Text joinedOutput = new Text(); 
    private String storeId;
    private String locationDetails;
//    private double totalSales;
    private Vector<String> listSalesDetails;
    
    @Override
    protected void reduce(Text key, Iterable<Text> values,
            Context context) throws IOException, InterruptedException {
        Iterator<Text> iterator = values.iterator();
        //key - store id
        Text value;
        storeId = key.toString();
//        totalSales = 0d;
        listSalesDetails = new Vector<String>();
        System.out.println("Reducer key:" +storeId);
        while(iterator.hasNext()) {
            value = iterator.next();
            System.out.println("Reducer value:" +value);
            String[] elements = value.toString().split(",");
            if(elements[0].equals("LOC")) {
                //Came from Store Location mapper..
                locationDetails = elements[1] + "," + elements[2];
            } else {
                //Came from Store Sales mapper..
//                totalSales += Double.parseDouble(elements[2]);
                listSalesDetails.add(elements[1] + "," + elements[2]);
            }
        }
        System.out.println("Reducer-----------------------");
//        joinedOutput.set(storeId + "," + locationDetails + "," + totalSales);
//        System.out.println("Reducer writing: " + joinedOutput.toString());
//        context.write(null,joinedOutput);
        
        for(String sale: listSalesDetails) {
            joinedOutput.set(storeId + "," + locationDetails + "," + sale);
            System.out.println("Reducer writing: " + joinedOutput.toString());
            context.write(null,joinedOutput);
        }
        
    }
}
