package com.ribomation.expression_tracer;

/**
 * Atomic test-and-set operation.
 */
public class AtomicLatch {
    private boolean     active = false;

    /**
     * Sets the atomic flag.
     */
    public synchronized void    set() {
        active = true;
    }

    /**
     * Unsets the atomic flag and returns the value before the unset.
     * @return prev value
     */
    public synchronized boolean isSet() {
        boolean result = active;
        active = false;
        return result;
    }
}
