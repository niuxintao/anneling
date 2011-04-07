package driver;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import simulating.Process;

/**
 * 输入是温度和递减参数的文本
 * */
public class Traggle extends Configured implements Tool {
	public int numMaps = 100;
	static private final Path TMP_DIR = new Path("myAnnelDir");

	public static class Map extends
			Mapper<LongWritable, Text, Text, LongWritable> {

		protected void map(
				LongWritable key,
				Text value,
				org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, LongWritable>.Context context)
				throws java.io.IOException, InterruptedException {
		      String line = value.toString();
		      StringTokenizer itr = new StringTokenizer(line);
		      double T=Double.parseDouble(itr.nextToken());
		      double decrement=Double.parseDouble(itr.nextToken());
		      Process t=new Process(T,decrement);
		      t.process();
		      Text keyTime=new Text("time"+value);
		      LongWritable valueTime=new LongWritable(t.time);
		      context.write(keyTime, valueTime);
		      Text keyLength=new Text("length"+value);
		      LongWritable valueLength=new  LongWritable(t.rsTable.length);
		      context.write(keyLength, valueLength);		      
		}
	}

	public static class Reduce extends
			Reducer<Text, LongWritable, Text, LongWritable> {
		public void reduce(Text key, Iterable<DoubleWritable> values,
				Reducer<Text, DoubleWritable, Text, DoubleWritable>.Context context)
				throws IOException, InterruptedException {
			for (DoubleWritable val : values) {
				context.write(new Text(" "), val);
			}
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Job job = new Job(getConf());
		job.setJarByClass(Traggle.class);
		job.setJobName("Anneling Process");
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(TMP_DIR, args[0]));
		FileOutputFormat.setOutputPath(job, new Path(TMP_DIR, args[1]));

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
}
