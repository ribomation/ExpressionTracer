package com.ribomation.expression_tracer;

/**
 * Thrown if an expression evaluation has failed.
 */
public class FailedEvaluation extends RuntimeException {
    public FailedEvaluation(String metric, String expr, Object target, Throwable cause) {
        super("Metric="+metric + ", expr=" + expr + ", target="+target, cause);
    }
}
