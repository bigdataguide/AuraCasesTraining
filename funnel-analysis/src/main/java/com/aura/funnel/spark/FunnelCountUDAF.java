package com.aura.funnel.spark;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.*;

public class FunnelCountUDAF extends UserDefinedAggregateFunction {

    private static Map<String, Map<Integer, Integer>> event_pos =
            new HashMap<>();

    private void initEvents(String events) {
        Map<Integer, Integer> pos = new HashMap<>();
        String[] es = events.split(",");
        for (int i=0;i<es.length;i++) {
            pos.put(Integer.parseInt(es[i]), i);
        }
        event_pos.put(events, pos);
    }

    @Override
    public StructType inputSchema() {
        return new StructType()
                .add("event_id", DataTypes.IntegerType)
                .add("timestamp", DataTypes.LongType)
                .add("window", DataTypes.LongType)
                .add("events", DataTypes.StringType);
    }

    @Override
    public StructType bufferSchema() {
        return new StructType()
                .add("found_first", DataTypes.BooleanType)
                .add("event_count", DataTypes.IntegerType)
                .add("window", DataTypes.LongType)
                .add("event_ts_list", DataTypes.createArrayType(DataTypes.LongType));
    }

    @Override
    public DataType dataType() {
        return DataTypes.IntegerType;
    }

    @Override
    public boolean deterministic() {
        return true;
    }

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, false); // contain first event
        buffer.update(1, 0); // event count
        buffer.update(2, 0L); // window
        buffer.update(3, new ArrayList<Long>()); // event timestamp
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        Integer eventId = input.getInt(0);
        Long timestamp = input.getLong(1);
        Long window = input.getLong(2);
        String events = input.getString(3);
        if (!event_pos.containsKey(events)) {
            initEvents(events);
        }
        Integer idx = event_pos.get(events).get(eventId);
        if (idx == 0) {
            buffer.update(0, true);
        }
        buffer.update(1, event_pos.get(events).size());
        buffer.update(2, window);
        List<Long> result = new ArrayList<>(buffer.getList(3));
        result.add(timestamp * 10 + idx);
        buffer.update(3, result);
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        buffer1.update(0, buffer1.getBoolean(0) || buffer2.getBoolean(0));
        buffer1.update(1, buffer2.getInt(1));
        buffer1.update(2, buffer2.getLong(2));
        List<Long> l1 = buffer1.getList(3);
        List<Long> l2 = buffer2.getList(3);
        if (l1.isEmpty()) {
            buffer1.update(3, l2);
        } else {
            List<Long> result = new ArrayList<>();
            result.addAll(l1);
            result.addAll(l2);
            buffer1.update(3, result);
        }
    }

    @Override
    public Object evaluate(Row buffer) {
        boolean foundFirst = buffer.getBoolean(0);
        int eventCount = buffer.getInt(1);
        if (!foundFirst) {
            return 0;
        }
        Long window = buffer.getLong(2);
        List<Long> eventTsList = new ArrayList<>(buffer.getList(3));
        Collections.sort(eventTsList);

        List<long[]> temp = new ArrayList<>();

        int max_event = 0;
        for (long eventTs: eventTsList) {
            long ts = eventTs / 10;
            byte event = (byte)(eventTs % 10);
            if (event == 0) {
                long[] flag = {ts, event};
                temp.add(flag);
            } else {
                for (int i = temp.size() - 1; i>=0; --i) {
                    long[] flag = temp.get(i);
                    if ((ts - flag[0]) >= window) {
                        break;
                    } else if (event == (flag[1] + 1)) {
                        flag[1] = event;
                        if (max_event < event) {
                            max_event = event;
                        }
                        break;
                    }
                }
                if ((max_event + 1) == eventCount) {
                    break;
                }
            }
        }
        return max_event + 1;
    }
}
