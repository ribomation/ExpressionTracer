package com.ribomation.expression_tracer.recorders;

import com.wily.introscope.agent.stat.*;

/**
 * String (value) = string value per interval
 */
public class TextRecorder extends AbstractRecorder {
    public TextRecorder(DataAccumulatorFactory f, String name) {
        super(f, name);
        setLast("");
    }

    protected IDataAccumulator create(DataAccumulatorFactory f, String name) throws Exception {
        return f.safeGetStringEveryEventDataAccumulator(name);
    }

    protected void add(IDataAccumulator metric, Object value) {
        ( (IStringEveryEventDataAccumulator) metric ).IStringEveryEventDataAccumulator_addString( toString(value) );
    }

}
