package driver;

import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import simulating.DataCenter;
import simulating.Process;

/**
 * 每一组参数运行100遍
 * */
public class Traggle extends Configured implements Tool {
	private int tasks = 1;
	static public int numMaps = 5;
	static public final int turns = 5;
	static public double[] temperture = new double[turns];
	static public double[] decrease = new double[turns];
	static private Random randomGenerator = new Random(); // 随机生成器
	static private final Path TMP_DIR = new Path("myAnnelDir");

	private static void initParameter() {
		for (int i = 0; i < turns; i++) {
			temperture[i] = DataCenter.coveringArrayNum
					* randomGenerator.nextDouble();
			decrease[i] = randomGenerator.nextDouble();
		}
	}

	public static class Map extends
			Mapper<LongWritable, LongWritable, Text, Text> {

		protected void map(
				LongWritable key,
				LongWritable value,
				Context context)
				throws java.io.IOException, InterruptedException {
			String line = context.getConfiguration().get("paramAnnel")
					.toString();
			StringTokenizer itr = new StringTokenizer(line);
			double T = Double.parseDouble(itr.nextToken());
			double decrement = Double.parseDouble(itr.nextToken());
			Process t = new Process(T, decrement);
			t.process();
			context.write(new Text("time"), new Text(t.time + ""));
			context.write(new Text("length"), new Text(t.rsTable.length + ""));
			String RTable=new String();
			for(int i=0;i<t.rsTable.length;i++)
			{
				for(int j=0;j<t.rsTable[i].length;j++)
				{
					RTable+=t.rsTable[i][j]+"\t";
				}
				RTable+="\r\n";
			}
			context.write(new Text("table"), new Text(RTable));
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values,Context context)
				throws IOException, InterruptedException {
			context.write(new Text("T adn Decrease"),new Text(context.getConfiguration().get("paramAnnel")));
			context.write(new Text("100 times" + key), new Text());
			if (key.equals(new Text("table"))) {
				for (Text val : values) {
					context.write(new Text(" "), val);
				}
			} else {
				long sum = 0;
				for (Text val : values) {
					sum += Integer.parseInt(val.toString());
					context.write(new Text(" "), val);
				}
				sum /= numMaps;
				context.write(new Text(key + " avg:"), new Text(sum + ""));
			}
		}
	}

	public void configMyWork(Job job) {
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setNumReduceTasks(tasks);
	}

	public void initInput(Job job) throws IOException {
		final FileSystem fs = FileSystem.get(job.getConfiguration());
		try {
			// generate an input file for each map task
			for (int i = 0; i < numMaps; ++i) {
				final Path file = new Path(new Path(TMP_DIR, "test-in"), "part"
						+ i);
				final LongWritable offset = new LongWritable(i);
				final LongWritable size = new LongWritable(1);
				final SequenceFile.Writer writer = SequenceFile.createWriter(
						fs, job.getConfiguration(), file, LongWritable.class,
						LongWritable.class, CompressionType.NONE);
				try {
					writer.append(offset, size);
				} finally {
					writer.close();
				}
				System.out.println("Wrote input for Map #" + i);
			}
		} finally {

		}
	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		for (int i = 0; i < turns; i++) {
			for (int j = 0; j < turns; j++) {
				Configuration conf = new Configuration();
				conf.setInt("step", i);
				conf.set("paramAnnel", temperture[i] + " " + decrease[j]);
				Job job = new Job(conf, "myWork" + i+"_"+j);
				configMyWork(job);
				FileInputFormat.setInputPaths(job, new Path(TMP_DIR, args[0]));
				FileOutputFormat.setOutputPath(job, new Path(TMP_DIR, args[1]
						+ "" + i+"_"+j));
				initInput(job);
				job.waitForCompletion(true);
			}
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int param[] = { 5, 5, 5, 5 };
		DataCenter.init(param, 2);
		initParameter();
		args = new String[] { "test-in", "test-out" };
		System.exit(ToolRunner.run(null, new Traggle(), args));
	}
}
