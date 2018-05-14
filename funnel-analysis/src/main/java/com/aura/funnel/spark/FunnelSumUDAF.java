package com.aura.funnel.spark;

import com.google.common.collect.Lists;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.*;

public class FunnelSumUDAF extends UserDefinedAggregateFunction {
    @Override
    public StructType inputSchema() {
        return new StructType()
                .add("events", DataTypes.IntegerType)
                .add("cnt", DataTypes.IntegerType);

    }

    @Override
    public StructType bufferSchema() {
        return new StructType()
                .add("events", DataTypes.IntegerType)
                .add("counts", DataTypes.createArrayType(DataTypes.LongType));
    }

    @Override
    public DataType dataType() {
        return DataTypes.createArrayType(DataTypes.LongType);
    }

    @Override
    public boolean deterministic() {
        return true;
    }

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, 0);
        buffer.update(1, new ArrayList<Long>());
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        int events = input.getInt(0);
        buffer.update(0, events);
        List<Long> cnts = new ArrayList<>();
        List<Long> buffList = buffer.getList(1);
        int maxEvent = input.getInt(1);
        for(int i=0;i<events;i++){
            long incr = i <= maxEvent ? 1L : 0L;
            if (buffList.isEmpty()) {
                cnts.add(incr);
            } else {
                cnts.add(buffList.get(i) + incr);
            }
        }
        buffer.update(1, cnts);
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        int events = buffer2.getInt(0);
        buffer1.update(0, events);
        List<Long> l1 = buffer1.getList(1);
        List<Long> l2 = buffer2.getList(1);
        if (l1.isEmpty()) {
            buffer1.update(1, l2);
        } else {
            List<Long> result = new ArrayList<>(l1.size());
            for (int i=0;i<events;i++) {
                result.add(l1.get(i) + l2.get(i));
            }
            buffer1.update(1, result);
        }
    }

    @Override
    public Object evaluate(Row buffer) {
        return buffer.getList(1);
    }
}
