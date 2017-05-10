package org.chandan.hadoop.pig.sample;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class SampleUdf extends EvalFunc<String>{

    @Override
    public String exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0){
            return "";
        }
        String result = tuple.toString();
        System.out.println("SampleUdf#exec()");
        System.out.println("SampleUdf#exec() i/p: " + result);
        for(Object entry: tuple.getAll()) {
            result += entry.toString();
        }
        System.out.println("SampleUdf#exec() o/p: " + result);
        return result;
    }
}
