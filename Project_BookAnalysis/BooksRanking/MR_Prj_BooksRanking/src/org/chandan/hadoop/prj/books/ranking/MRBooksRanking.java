package org.chandan.hadoop.prj.books.ranking;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MRBooksRanking {

    /**
     * i/p : BX-Books.csv
     * <ISBN    Book-Title  Book-Author Year-Of-Publication Publisher   Image-URL-S Image-URL-M Image-URL-L>
     * @author m1033286
     *
     */
    public static class MyMapperForYear extends Mapper<LongWritable, Text, Text, Text> {

        private Text outputKey = new Text();
        private Text outputValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                final String[] attributes = value.toString().split(";");
                final String year = attributes[3].replaceAll("\"", "");
                if(year.length() == 4 && year.matches("[0-9]*")) {
                    final String ISBN = attributes[0].replaceAll("\"", "");
                    outputKey.set(ISBN);
                    outputValue.set(year);
                    System.out.print("MyMapperForYear: <" + outputKey.toString() +", " + outputValue.toString() +">");
                    context.write(outputKey, outputValue);
                }
//                else {
//                    System.out.print(" Not Mapping - year:"+year);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * i/p : BX-Book-Ratings.csv
     * <User-ID ISBN    Book-Rating>
     * @author m1033286
     *
     */
    public static class MyMapperForRating extends Mapper<LongWritable, Text, Text, Text> {

        private Text outputKey = new Text();
        private Text outputValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                final String[] attributes = value.toString().split(";");
                final String ISBN = attributes[1].replaceAll("\"", "");
                final String rating = attributes[2].replaceAll("\"", "");
                outputKey.set(ISBN);
                outputValue.set(rating);
                System.out.print("MyMapperForRating: <" + outputKey.toString() +", " + outputValue.toString() +">");
                context.write(outputKey, outputValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * o/p : <Rating,1>
     * @author m1033286
     *
     */
    public static class MyReducerForIsbnAndRatingJoin extends Reducer<Text, Text, Text, Text> {
        private HashMap<Integer,Integer> ratingVsBookCountMap = new HashMap<Integer,Integer>();
        private Text outputKey = new Text();
        private Text outputValue = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            boolean isPublishedYear2002 =false;
            int totalRating = 0;
            int noOfRatings = 0;
            for(Text entry:values) {
                if(entry.toString().length() == 4) {
                    isPublishedYear2002 = "2002".equalsIgnoreCase(entry.toString());
                } else {
                    try {
                        totalRating+=Integer.parseInt(entry.toString());
                        noOfRatings++;
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            final int avgRanking = (int)(totalRating/(float)noOfRatings);
            System.out.println("MyReducerForIsbnAndRatingJoin: [" + key.toString() +", " + isPublishedYear2002 + "," + avgRanking +"]");
            if(isPublishedYear2002) {
                Integer currentCount = ratingVsBookCountMap.get(avgRanking);
                currentCount = currentCount == null ? 0 :currentCount;
                ratingVsBookCountMap.put(avgRanking, currentCount+1);
                System.out.println("MyReducerForIsbnAndRatingJoin: Updating Map: Adding one more book to rating entry: " + avgRanking);
            }
        }

        @Override
        protected void cleanup(Context context)
                throws IOException, InterruptedException {
            System.out.println("MyReducerForIsbnAndRatingJoin#cleanup()");
            for(Entry<Integer, Integer> entry: ratingVsBookCountMap.entrySet()) {
                outputKey.set("" + entry.getKey());
                outputValue.set("" + entry.getValue());
                System.out.println(outputKey.toString() +"\t"+outputValue.toString());
                context.write(outputKey, outputValue);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"MRBooksRanking");
        job.setJarByClass(MRBooksRanking.class);

        job.setReducerClass(MyReducerForIsbnAndRatingJoin.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, MyMapperForYear.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, MyMapperForRating.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
