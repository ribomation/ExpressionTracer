package com.ribomation.expression_tracer;

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.trace.ASingleMetricTracerFactory;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.introscope.agent.trace.DefaultNameFormatter;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.Module;
import com.wily.util.feedback.SimpleModuleFeedbackChannel;
import com.wily.util.properties.AttributeListing;
import com.wily.util.StringUtils;
import com.wily.util.heartbeat.ITimestampedRunnable;
import com.wily.util.heartbeat.IntervalHeartbeat;
import com.ribomation.expression_tracer.recorders.RecorderFactory;

import java.io.*;
import java.util.Properties;

/**
 * A tracer that can evaluate an OGNL invocation expression on the instrumented object or
 * any of its method arguments or return value.
 */
public class ExpressionTracer extends ASingleMetricTracerFactory implements ITimestampedRunnable {
    private Module                          module = new Module("ExpressionTracer");
    private IModuleFeedbackChannel          log;
    private ExpressionHolder                expr;
    private Recorder                        recorder;
    private AtomicLatch                     hasReported = new AtomicLatch();

    /**
     * Creates the tracer and parses the metric input.
     * @param agent         context
     * @param props         tracer parameters
     * @param probeId       probe info
     * @param target        sample target object
     */
    public ExpressionTracer(IAgent agent, AttributeListing props, ProbeIdentification probeId, Object target) {
        super(agent, props, probeId, target);

        log    = new SimpleModuleFeedbackChannel(agent.IAgent_getModuleFeedback(), module.getName());

        File        agentDir = new File( agent.IAgent_getConfigurationResource().IResource_getLocation() ).getParentFile();
        File        cfgFile  = new File(agentDir, "ExpressionTracer.properties");
        Properties  cfg      = new Properties();
        if (cfgFile.canRead()) {
            try {
                InputStream in = new FileInputStream(cfgFile);
                cfg.load(in);
                cfg.clone();
                log.verbose("External Expressions = " + cfg);
            } catch (Exception e) {
                log.warn("Failed to read config file: "+cfgFile, e);
            }
        }

        String metricFullName = getNameParameter();
        log.verbose("Input = " + metricFullName);
        expr   = new ExpressionHolder(metricFullName, cfg);
        log.verbose("Expression = " + expr);

        String   mName           = new DefaultNameFormatter().ICachedNameFormatter_format(expr.getMetricName(), probeId, target);
        String   type            = getParameterAsString("metricType", "average");
        boolean  retainLastValue = getParameterAsBoolean("retainLastValue", false);
        recorder                 = new RecorderFactory().create(getDataAccumulatorFactory(), type, mName);

        log.info("Created: metric=" + mName + ", type=" + type + ", retainLastValue=" + retainLastValue + ", target=" + target.getClass().getName() + ", expression=" + expr.getExpression());
        log = new SimpleModuleFeedbackChannel(agent.IAgent_getModuleFeedback(), module.getName() + "#" + mName.replace(' ', '_'));

        if (retainLastValue) {
            log.info("Starting retention task");
            agent.IAgent_getCommonHeartbeat().addBehavior(
                    this,
                    module.getName()+"#"+mName,
                    IntervalHeartbeat.kActive,
                    7500,
                    IntervalHeartbeat.kRunFirst
            );
        }
    }

    /**
     * Called before the instrumented method, but not used in this tracer.
     * @param i
     * @param data
     */
    public void ITracer_startTrace(int i, InvocationData data) {
        //do nothing
    }

    /**
     * Evaluates the OGNL expression at the target object and reports the result as a metric value.
     * @param i                 not used
     * @param invocationData    invocation context
     */
    public void ITracer_finishTrace(int i, InvocationData invocationData) {
        try {
            Object   valueObject = expr.eval(invocationData);
            recorder.add(valueObject);
            hasReported.set();

            log.verbose("value=" + valueObject);
        } catch (Exception e) {
            log.verbose("Failed to evaluate expression", e);
        }
    }

    public void ITimestampedRunnable_execute(long l) {
        log.verbose("RetainTask: ENTER");
        if (hasReported.isSet()) return;

        log.verbose("RetainTask: ADD LAST VALUE = " + recorder.getLast());
        recorder.addLast();
    }

    /**
     * Returns the already created metric object.
     * @param metricName    name
     * @return metric
     */
    protected IDataAccumulator createDataAccumulator(String metricName) {
         return null;
    }

    protected String   getParameterAsString(String name, String defaultValue) {
        String  value = this.getParameter(name);
        if (StringUtils.isEmpty(value)) return defaultValue;

        return value;
    }

     protected boolean getParameterAsBoolean(String name, boolean defaultValue) {
        String  value = this.getParameter(name);
        if (StringUtils.isEmpty(value)) return defaultValue;

        return value.equalsIgnoreCase("true");
    }

}
