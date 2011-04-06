package driver;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;

public class Traggle extends Configured implements Tool {
	public static class Map extends
			Mapper<LongWritable, LongWritable, Text, DoubleWritable> {

	}

	public static class Reduce extends
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
