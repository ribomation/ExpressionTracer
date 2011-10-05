package com.wily.introscope.agent.trace;

/**
 * Mock InvocationData.
 */
public class InvocationData {
    private Object              target;
    private boolean             hasParams = true;
    private ProbeInformation    info;
    private Object              arg;

    public InvocationData(Object target, String method, Object arg) {
        this.target = target;
        this.info   = new ProbeInformation(new ProbeIdentification(method));
        this.arg    = arg;
    }

    public Object       getInvocationObject() {
        return target;
    }

    public Object       getInvocationParameterAsObject(int idx) {
        return arg;
    }

    public Object       getInvocationReturnValueAsObject() {
        return arg;
    }

    public boolean      hasInvocationParametersAvailable() {
        return hasParams;
    }

    public ProbeInformation     getProbeInformation() {
        return info;
    }
}
