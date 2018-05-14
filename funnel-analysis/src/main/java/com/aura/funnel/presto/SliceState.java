package com.aura.funnel.presto;

import com.facebook.presto.spi.function.AccumulatorState;
import io.airlift.slice.Slice;

public interface SliceState extends AccumulatorState {
    Slice getSlice();
    void setSlice(Slice slice);
}
