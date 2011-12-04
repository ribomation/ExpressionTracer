package com.wily.introscope.agent.trace;

/**
 * Mock ProbeInformation
 */
public class ProbeInformation {
    private ProbeIdentification     id;

    public ProbeInformation(ProbeIdentification id) {
        this.id = id;
    }

    public ProbeIdentification      getProbeIdentification() {
        return id;
    }
}
