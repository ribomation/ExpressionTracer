package com.ribomation.expression_tracer.recorders;

import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.ILongIntervalCounterDataAccumulator;

/**
 * Sum = the sum over one interval
 */
public class SumRecorder extends AbstractRecorder {
    public SumRecorder(DataAccumulatorFactory f, String name) {
        super(f, name);
        setLast(new Long(0));
    }

    protected IDataAccumulator create(DataAccumulatorFactory f, String name) throws Exception {
        return f.safeGetLongIntervalCounterDataAccumulator(name);
    }

    protected void add(IDataAccumulator metric, Object value) {
        ( (ILongIntervalCounterDataAccumulator)metric ).ILongIntervalCounterDataAccumulator_addBatchIncidents( toLong(value) );
    }

}
