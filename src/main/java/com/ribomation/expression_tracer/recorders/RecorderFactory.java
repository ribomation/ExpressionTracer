package com.ribomation.expression_tracer.recorders;

import com.ribomation.expression_tracer.InvalidExpression;
import com.ribomation.expression_tracer.MetricType;
import com.ribomation.expression_tracer.Recorder;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;

import static com.ribomation.expression_tracer.MetricType.*;

/**
 * Creates a recorder based on type and metric name.
 */
public class RecorderFactory {

    public Recorder create(DataAccumulatorFactory f, MetricType type, String name) {
        if (type == average) return new AverageRecorder(f, name);
        if (type == counter) return new AverageRecorder(f, name);
        if (type == last)    return new AverageRecorder(f, name);
        if (type == sum)     return new AverageRecorder(f, name);
        if (type == text)    return new AverageRecorder(f, name);
        throw new InvalidExpression("Invalid metric type: " + type);
    }

}
