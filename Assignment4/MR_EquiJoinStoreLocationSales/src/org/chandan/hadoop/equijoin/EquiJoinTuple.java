package org.chandan.hadoop.equijoin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class EquiJoinTuple implements WritableComparable<EquiJoinTuple> {

    private int storeId;
    private int dataSet;//Unique id for each input data sets.

    public EquiJoinTuple() {
        
    }

    public EquiJoinTuple(int store,int set) {
        super();
        storeId = store;
        dataSet = set;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getDataSet() {
        return dataSet;
    }

    public void setDataSet(int dataSet) {
        this.dataSet = dataSet;
    }
    
    @Override
    public int hashCode() {
        final int SEED = 21;
        int hashcode = 1;

        hashcode *= SEED;
        hashcode += getStoreId();

        hashcode *= SEED;
        hashcode += getDataSet();
        
        return hashcode;
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        setStoreId(input.readInt());
        setDataSet(input.readInt());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(getStoreId());
        output.writeInt(getDataSet());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EquiJoinTuple target = (EquiJoinTuple) obj;
        if(getDataSet() != target.getDataSet()) {
            return false;
        }
        if(getStoreId() != target.getStoreId()) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(EquiJoinTuple target) {
        int result = compare(storeId, target.getStoreId());
        if (result != 0) {
            return result;
        }
        return compare(dataSet, target.getDataSet());
    }

    public static int compare(int value1, int value2) {
        return (value1 < value2 ? -1 : (value1 == value2 ? 0 : 1));
    }

    @Override
    public String toString() {
        return "EquiJoinTuple [Store ID = " + getStoreId() + ", DataSet = " + getDataSet() + "]";
    }

}
