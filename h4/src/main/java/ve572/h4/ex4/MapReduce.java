package ve572.h4.ex4;

import org.apache.avro.Schema;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroKeyValueOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;

import java.io.IOException;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class MapReduce {

    public static class Map extends Mapper<Object, Text, Text, IntWritable> {
        private Text id = new Text();
        private IntWritable score = new IntWritable();
        private BloomFilter filter = new BloomFilter(1, 10, 1);

        public void setup(Context context) {
            filter.add(new Key("3".getBytes()));
        }

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");
            tokenizer.nextToken();
            String _id = tokenizer.nextToken();
            String _idLast = _id.substring(_id.length() - 1);
            if (!filter.membershipTest(new Key(_idLast.getBytes()))) {
                return;
            }
            id.set(_id);
            score.set(parseInt(tokenizer.nextToken()));
            context.write(id, score);
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, AvroKey<String>, AvroValue<Integer>> {

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int score = 0;
            for (IntWritable val : values) {
                score = Math.max(score, val.get());
            }
            context.write(new AvroKey<>(key.toString()), new AvroValue<>(score));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ex4");
        job.setJarByClass(MapReduce.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputFormatClass(AvroKeyValueOutputFormat.class);

        AvroJob.setOutputKeySchema(job, Schema.create(Schema.Type.STRING));
        AvroJob.setOutputValueSchema(job, Schema.create(Schema.Type.INT));

        FileInputFormat.addInputPath(job, new Path(args[0]));
        Path outputPath = new Path(args[1]);
        FileSystem fileSystem = outputPath.getFileSystem(conf);
        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        long startTime = System.currentTimeMillis();
        boolean exitCode = job.waitForCompletion(true);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime + " ms");

        System.exit(exitCode ? 0 : 1);
    }
}
