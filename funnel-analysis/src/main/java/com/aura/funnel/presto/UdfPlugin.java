package com.aura.funnel.presto;

import com.facebook.presto.spi.Plugin;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class UdfPlugin implements Plugin {

    @Override
    public Set<Class<?>> getFunctions() {
        return ImmutableSet.<Class<?>>builder()
                .add(AggregationLDCount.class)
                .add(AggregationLDSum.class)
                .build();
    }
}
