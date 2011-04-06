package driver;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

public class Traggle extends Configured implements Tool {
	public int numMaps = 100;
	static private final Path TMP_DIR = new Path("myAnnelDir");

	public static class Map extends
			Mapper<LongWritable, LongWritable, Text, DoubleWritable> {

	}

	public static class Reduce extends
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Job job = new Job(getConf());
		job.setJarByClass(Traggle.class);
		job.setJobName("Anneling Process");
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		initInput(job);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public void initInput(Job job) throws IOException {
		final FileSystem fs = FileSystem.get(job.getConfiguration());
		try {
			// generate an input file for each map task
			for (int i = 0; i < numMaps; ++i) {
				final Path file = new Path(new Path(TMP_DIR, "test-in"), "part"
						+ i);
				final LongWritable offset = new LongWritable(i);
				final LongWritable size = new LongWritable(i);
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

	public void clearDir(Job job) throws IOException {
		final FileSystem fs = FileSystem.get(job.getConfiguration());
		if (fs.exists(TMP_DIR))
			fs.delete(TMP_DIR, true);
	}

}
