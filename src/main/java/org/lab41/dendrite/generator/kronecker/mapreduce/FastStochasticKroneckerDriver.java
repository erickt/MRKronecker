package org.lab41.dendrite.generator.kronecker.mapreduce;

import com.thinkaurelius.faunus.FaunusVertex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.lab41.dendrite.generator.kronecker.mapreduce.lib.input.FastKroneckerInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * 
 * Drives a fast MapReduce implementation of the KronGen algorithm.
 * 
 * @author kramachandran
 */
public class FastStochasticKroneckerDriver extends BaseDriver implements Tool {
    Logger logger = LoggerFactory.getLogger(StochasticKroneckerDriver.class);
    
    @Override
    public Job configureGeneratorJob(Configuration conf) throws IOException {
        /** configure Job **/
        Job job = new Job(getConf());
        job.setJobName("FastStochasticKronecker N=" + Integer.toString(n));
        job.setJarByClass(StochasticKroneckerDriver.class);

        /** Set the Mapper & Reducer**/
        job.setMapperClass(FastStochasticKroneckerMapper.class);
        job.setCombinerClass(FaunusVertexCombiner.class);
        job.setReducerClass(FaunusVertexAnnotatingReducer.class);

        /* Configure Input Format to be our custom InputFormat */
        job.setInputFormatClass(FastKroneckerInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileOutputFormat.setOutputPath(job, outputPath);

        /* Configure Map Output */
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(FaunusVertex.class);

        /* Configure job (Reducer) output */
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(FaunusVertex.class);

        //Set the configuration
        job.getConfiguration().set(Constants.PROBABILITY_MATRIX, initiator);
        job.getConfiguration().set(Constants.N, Integer.toString(n));
        job.getConfiguration().set(Constants.BLOCK_SIZE, Long.toString((long) Math.pow(2, 20)));
        return job;
    }

    /**
     * Runs this tool using the provided command-line arguments
     * @param args Command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new FastStochasticKroneckerDriver(), args);

        System.exit(exitCode);
    }
}
