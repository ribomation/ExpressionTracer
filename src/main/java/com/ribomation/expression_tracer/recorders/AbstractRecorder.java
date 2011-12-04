package com.ribomation.expression_tracer.recorders;

import com.ribomation.expression_tracer.InvalidExpression;
import com.ribomation.expression_tracer.Recorder;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;

/**
 * Base class recorder providing most methods from interface {@link com.ribomation.expression_tracer.Recorder}.
 * Sub-classes need to implement
 * {@link #create(com.wily.introscope.agent.stat.DataAccumulatorFactory, String)} and
 * {@link #add(Object)}.
 */
public abstract class AbstractRecorder implements Recorder {
    private IDataAccumulator   metric;
    private String             type;
    private Object             lastValue;

    protected AbstractRecorder(DataAccumulatorFactory f, String name) {
        try {
            metric = create(f, name);
            type   = extractType(this);
        } catch (Exception e) {
            throw new InvalidExpression("Cannot create "+this.getClass().getName(), e);
        }
    }

    protected abstract IDataAccumulator  create(DataAccumulatorFactory f, String name) throws Exception;
    protected abstract void              add(IDataAccumulator metric, Object value);

    public final void              add(Object value) {
        add(getMetric(), value);
        setLast(value);
    }

    public void addLast() {
        add(getMetric(), lastValue);
    }

    protected void  setLast(Object v) {
        lastValue = v;
    }

    public Object   getLast() {
        return lastValue;
    }

    public String getName() {
        return getMetric().IDataAccumulator_getMetric().getAttributeName();
    }

    public String getType() {
        return type;
    }

    protected IDataAccumulator getMetric() {
        return metric;
    }

    protected String  extractType(Object r) {
        String  n = r.getClass().getName();
        int     s = n.lastIndexOf('.') + 1;
        int     e = n.length() - "Recorder".length();
        return n.substring(s, e);
    }

    protected long  toLong(Object v) {
        if (v == null) return 0;

        if (v instanceof Number) {
            return ((Number) v).longValue();
        }

        try {
            return Long.parseLong( v.toString() );
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected boolean toBool(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean) {
            return ((Boolean) v).booleanValue();
        }
        return Boolean.parseBoolean( v.toString() );
    }

    protected String toString(Object v) {
        if (v != null) {
            return v.toString();
        }
        return "";
    }
}
