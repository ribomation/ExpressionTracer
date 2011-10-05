package com.ribomation.expression_tracer;

/**
 * Thrown if an metric name expression cannot be parsed.
 */
public class InvalidExpression extends RuntimeException {
    public InvalidExpression(String expr) {
        super("Invalid metric & expression: " + expr);
    }

    public InvalidExpression(String expr, Throwable cause) {
        super("Invalid metric & expression: " + expr, cause);
    }
}
