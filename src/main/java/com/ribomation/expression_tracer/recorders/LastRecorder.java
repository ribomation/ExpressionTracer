package com.ribomation.expression_tracer.recorders;

import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.ILongFluctuatingCounterDataAccumulator;

/**
 * Last (value) = last value per interval
 */
public class LastRecorder extends AbstractRecorder {
    public LastRecorder(DataAccumulatorFactory f, String name) {
        super(f, name);
        setLast(new Long(0));
    }

    protected IDataAccumulator create(DataAccumulatorFactory f, String name) throws Exception {
        return f.safeGetLongFluctuatingCounterDataAccumulator(name);
    }

    protected void add(IDataAccumulator metric, Object value) {
        ( (ILongFluctuatingCounterDataAccumulator)metric ).ILongCounterDataAccumulator_setValue( toLong(value) );
    }

}
