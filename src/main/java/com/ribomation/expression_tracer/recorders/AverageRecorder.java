package com.ribomation.expression_tracer.recorders;

import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.ILongAverageDataAccumulator;

/**
 * Average = mean value of all values per interval
 */
public class AverageRecorder extends AbstractRecorder {
    public AverageRecorder(DataAccumulatorFactory f, String name) {
        super(f, name);
        setLast(new Long(0));
    }

    protected IDataAccumulator create(DataAccumulatorFactory f, String name) throws Exception {
        return f.safeGetLongAverageDataAccumulator(name);
    }

    protected void add(IDataAccumulator metric, Object value) {
        ( (ILongAverageDataAccumulator)metric ).ILongAggregatingDataAccumulator_recordDataPoint( toLong(value) );
    }
}
