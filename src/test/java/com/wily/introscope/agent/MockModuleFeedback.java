package com.wily.introscope.agent;

import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.Module;
import com.wily.util.feedback.SeverityLevel;

/**
 * Empty IModuleFeedbackChannel
 */
public class MockModuleFeedback implements IModuleFeedbackChannel {

    public boolean isTraceEnabled() {
        return false;
    }

    public void debug(Module module, String string) {}

    public void debug(Module module, String string, Throwable throwable) {}

    public void debug(String string) {}

    public void debug(String string, Throwable throwable) {}

    public void debug(Throwable throwable) {}

    public void error(Module module, String string) {}

    public void error(Module module, String string, Throwable throwable) {}

    public void error(String string) {}

    public void error(String string, Throwable throwable) {}

    public void error(Throwable throwable) {}

    public String getApplicationName() {return null;}

    public String getModuleName() {return null;}

    public void info(Module module, String string) {}

    public void info(Module module, String string, Throwable throwable) {}

    public void info(String string) {}

    public void info(String string, Throwable throwable) {}

    public boolean isDebugEnabled() { return false; }

    public boolean isDebugEnabled(Module module) { return false; }

    public boolean isErrorEnabled(Module module) { return false; }

    public boolean isInfoEnabled(Module module) { return false; }

    public boolean isLogEnabled(SeverityLevel severityLevel, Module module) { return false; }

    public boolean isTraceEnabled(Module module) { return false; }

    public boolean isVerboseEnabled() { return false; }

    public boolean isVerboseEnabled(Module module) { return false; }

    public boolean isWarningEnabled(Module module) { return false; }

    public void log(SeverityLevel severityLevel, Module module, String string) {}

    public void log(SeverityLevel severityLevel, Module module, String string, Throwable throwable) {}

    public void log(SeverityLevel severityLevel, String string) {}

    public void log(SeverityLevel severityLevel, String string, Throwable throwable) {}

    public void logImmediate(SeverityLevel severityLevel, Module module, String string) {}

    public void logImmediate(SeverityLevel severityLevel, Module module, String string, Throwable throwable) {}

    public void trace(Module module, String string) {}

    public void trace(Module module, String string, Throwable throwable) {}

    public void verbose(Module module, String string) {}

    public void verbose(Module module, String string, Throwable throwable) {}

    public void verbose(String string) {}

    public void verbose(String string, Throwable throwable) {}

    public void verbose(Throwable throwable) {}

    public void warn(Module module, String string) {}

    public void warn(Module module, String string, Throwable throwable) {}

    public void warn(String string) {}

    public void warn(String string, Throwable throwable) {}

    public void trace(String s) {
    }

    public void setLevel(SeverityLevel severityLevel, Module module) {
    }

    public SeverityLevel getLevel(Module module) {
        return null;
    }

    public SeverityLevel getEffectiveLevel(Module module) {
        return null;
    }
}
