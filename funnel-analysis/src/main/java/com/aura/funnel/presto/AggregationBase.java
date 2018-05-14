package com.aura.funnel.presto;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import java.util.HashMap;
import java.util.Map;

public class AggregationBase {

    protected static Map<Slice, Map<Slice, Byte>> event_pos_dict
            = new HashMap<>();

    protected static void init_events(Slice events, int idx) {
        String[] array = new String(events.getBytes()).split(",");
        Map<Slice, Byte> index = new HashMap<>();
        for (int i=0;i<array.length;i++) {
            byte[] data = array[i].getBytes();
            Slice slice = Slices.allocate(data.length);
            slice.setBytes(0, data);
            index.put(slice, (byte)i);
        }
        event_pos_dict.put(events, index);
    }
}
