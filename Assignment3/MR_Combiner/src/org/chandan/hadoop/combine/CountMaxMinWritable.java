package org.chandan.hadoop.combine;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class CountMaxMinWritable implements Writable{

    private int count;
    private float max;
    private float min;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        setCount(input.readInt());
        setMax(input.readFloat());
        setMin(input.readFloat());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(getCount());
        output.writeFloat(getMax());
        output.writeFloat(getMin());
    }

    @Override
    public String toString() {
        return getCount() + "\t" + getMax() + "\t" + getMin();
    }
}
