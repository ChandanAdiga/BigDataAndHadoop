package org.chandan.hadoop.equijoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class EquiJoinPartitioner extends Partitioner<EquiJoinTuple, Text> {

    @Override
    public int getPartition(EquiJoinTuple equiJoinTuple, Text value, int numPartitions) {
        int partition = (equiJoinTuple.hashCode() & Integer.MAX_VALUE) % numPartitions;
        return partition;
    }
}