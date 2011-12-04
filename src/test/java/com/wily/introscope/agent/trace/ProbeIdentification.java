package com.wily.introscope.agent.trace;

/**
 * Mock ProbeIdentification
 */
public class ProbeIdentification {
    private String methodName;

    public ProbeIdentification(String methodName) {
        this.methodName = methodName;
    }

    public String   getProbeMethodName() {
        return methodName;
    }
}
