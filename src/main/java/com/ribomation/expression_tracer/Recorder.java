package com.ribomation.expression_tracer;

/**
 * Abstraction for an Introscope data accumulator.
 */
public interface Recorder {
    /**
     * Adds a value to the recorder.
     * @param value     value to add/record
     */
    void        add(Object value);

    /**
     * Returns the (final) metric name.
     * @return its metric name
     */
    String      getName();

    /**
     * Returns its metric type.
     * @return its type
     */
    String      getType();

    Object   getLast();
}
