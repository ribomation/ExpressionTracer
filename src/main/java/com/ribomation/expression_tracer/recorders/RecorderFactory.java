package com.ribomation.expression_tracer.recorders;

import com.ribomation.expression_tracer.InvalidExpression;
import com.ribomation.expression_tracer.Recorder;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;

/**
 * Creates a recorder based on type and metric name.
 */
public class RecorderFactory {

    public Recorder create(DataAccumulatorFactory f, String type, String name) {
        type = type.toLowerCase();
        if ("average".equals(type)) {
            return new AverageRecorder(f, name);
        } else if ("counter".equals(type)) {
            return new PerIntervalCounterRecorder(f, name);
        } else if ("last".equals(type)) {
            return new LastRecorder(f, name);
        } else if ("sum".equals(type)) {
            return new SumRecorder(f, name);
        } else if ("text".equals(type)) {
            return new TextRecorder(f, name);
        } else {
            throw new InvalidExpression("Invalid metric type: "+type);
        }
    }

}
