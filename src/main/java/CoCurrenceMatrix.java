import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by zzc on 8/4/17.
 */

public class CoCurrenceMatrix {
    final static Logger logger = Logger.getLogger(DataDividedByUser.class);

    public static class CoCurrenceMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String movies_ratings[] = value.toString().trim().split(",");

            for (String movie_rating : movies_ratings) {
                String movie0 = movie_rating.split("-")[0];
                for (String movie_rating1 : movies_ratings) {
                    String movie1 = movie_rating1.split("-")[0];
//                    System.out.println(movie0+"-"+movie1);
                    context.write(new Text(movie0 + "-" + movie1), new IntWritable(1));
                }
            }
        }
    }

//    public static class CoCurrenceReducer extends Reducer<Text, IntWritable, IntWritable, Text> {
//        @Override
//        protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//            StringBuffer buffer = new StringBuffer();
//            for (Text value : values) {
//                buffer.append(value + ",");
//            }
//            buffer.deleteCharAt(buffer.length() - 1);
//            System.out.println(key+" : "+buffer.toString());
//            context.write(key, new Text(buffer.toString()));
//        }
//    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String inputDataDir = args[0];
        String outputDataDir = args[1];

        File output2 = new File(outputDataDir);
        if (output2.exists()) {
            if (logger.isInfoEnabled()) {
                logger.info("Output2 directory already exits!\tDelete previous directory.");
            }
            FileUtils.deleteDirectory(output2);
        }

        Configuration conf= new Configuration();
        Job job = Job.getInstance(conf);
        job.setMapperClass(CoCurrenceMapper.class);
//        job.setReducerClass(CoCurrenceReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        TextInputFormat.setInputPaths(job, new Path(inputDataDir));
        TextOutputFormat.setOutputPath(job, new Path(outputDataDir));
        job.waitForCompletion(true);
    }
}
