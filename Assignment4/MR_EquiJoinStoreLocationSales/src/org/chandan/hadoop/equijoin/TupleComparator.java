package org.chandan.hadoop.equijoin;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TupleComparator extends WritableComparator {
    
    protected TupleComparator() {
        super(EquiJoinTuple.class, true);
    }

    @Override
    public int compare(@SuppressWarnings("rawtypes") WritableComparable w1,
            @SuppressWarnings("rawtypes") WritableComparable w2) {
        return EquiJoinTuple.compare(((EquiJoinTuple) w1).getStoreId(), ((EquiJoinTuple) w2).getStoreId());
    }
}