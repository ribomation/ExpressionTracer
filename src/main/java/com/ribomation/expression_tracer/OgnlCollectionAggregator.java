package com.ribomation.expression_tracer;

import java.io.Serializable;

/**
 * Set of various aggregation methods exposed to the OGNL expressions. 
 * <p/>
 * User: Jens
 * Date: 2011-11-30, 15:51
 */
public class OgnlCollectionAggregator implements Serializable {

    public long SUM(Object obj) {
        return aggregate(obj, 0, new Aggregator() {
            public long aggregate(Object obj, long accumulator) { return valueOf(obj) + accumulator; }
        });
    }
    public long PROD(Object obj) {
        return aggregate(obj, 1, new Aggregator() {
            public long aggregate(Object obj, long accumulator) { return valueOf(obj) * accumulator; }
        });
    }
    public long MIN(Object obj) {
        return aggregate(obj, Long.MAX_VALUE, new Aggregator() {
            public long aggregate(Object obj, long accumulator) {
                long value = valueOf(obj);
                return (value < accumulator ? value : accumulator); }
        });
    }
    public long MAX(Object obj) {
        return aggregate(obj, Long.MIN_VALUE, new Aggregator() {
            public long aggregate(Object obj, long accumulator) {
                long value = valueOf(obj);
                return (value > accumulator ? value : accumulator); }
        });
    }
    
    interface Aggregator {
        long aggregate(Object obj, long accumulator);
    }
    
    public long aggregate(Object obj, long initValue, Aggregator aggregator) {
        if (obj instanceof Iterable) {
            long accumulator = initValue;
            for (Object item : (Iterable) obj) accumulator = aggregator.aggregate(item, accumulator);
            return accumulator;
        }
        return 0;
    }

    private long valueOf(Object item) {
        if (item == null) return 0;
        Class<? extends Object> itemClass = item.getClass();
        if (itemClass == Long.TYPE) return (Long) item;
        if (itemClass == Integer.TYPE) return (Integer) item;
        if (itemClass == Short.TYPE) return (Short) item;
        if (itemClass == Byte.TYPE) return (Byte) item;
        if (Number.class.isAssignableFrom(itemClass)) return ((Number) item).longValue();
        return 0;
    }
    
}
