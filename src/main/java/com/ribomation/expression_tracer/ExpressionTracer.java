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
import com.wily.util.properties.IndexedProperties;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A tracer that can evaluate an OGNL invocation expression on the instrumented object or
 * any of its method arguments or return value.
 */
public class ExpressionTracer extends ASingleMetricTracerFactory {
    private static final String EXPRESSION_TRACER       = "ExpressionTracer";
    private static final String EXPRESSION_TRACER_FILE  = "ExpressionTracer.file";
    private static final String METRIC_TYPE             = "metricType";
    private static final String RETENTION_MODE          = "retentionMode";
    private IModuleFeedbackChannel  log;
    private ExpressionHolder        expr;
    private Recorder                recorder;
    private AtomicBoolean           hasReported = new AtomicBoolean(false);

    /**
     * Creates the tracer and parses the metric input.
     * @param agent         context
     * @param props         tracer parameters
     * @param probeId       probe info
     * @param target        sample target object
     */
    public ExpressionTracer(IAgent agent, AttributeListing props, ProbeIdentification probeId, Object target) {
        super(agent, props, probeId, target);
        log = new SimpleModuleFeedbackChannel(agent.IAgent_getModuleFeedback(), EXPRESSION_TRACER);
        log.verbose("Initializing");

        IndexedProperties   agentProperties = agent.IAgent_getIndexedProperties();
        String              expressionsFilename = agentProperties.getTrimmedProperty(EXPRESSION_TRACER_FILE, "ExpressionTracer.properties");
        File                agentDir = new File( agent.IAgent_getConfigurationResource().IResource_getLocation() ).getParentFile();
        Properties          expressions = loadExpressions(expressionsFilename, agentDir);

        String rawMetricDefinition = getNameParameter();
        log.verbose("Raw metric string = " + rawMetricDefinition);
        
        expr   = new ExpressionHolder(rawMetricDefinition, expressions);
        log.verbose("Extracted expression = " + expr);

        String     metricName = new DefaultNameFormatter().ICachedNameFormatter_format(expr.getMetricName(), probeId, target);
        log.verbose("Metric Name = " + metricName);
        
        MetricType metricType  = getParameterAsMetricType(METRIC_TYPE, MetricType.average);
        log.verbose("Metric Type = " + metricType);

        RetentionMode retentionMode = getParameterAsRetentionMode(RETENTION_MODE, RetentionMode.none);
        log.verbose("Retention Mode = " + retentionMode);

        recorder = new RecorderFactory().create(getDataAccumulatorFactory(), metricType, metricName);
        
        if (retentionMode != RetentionMode.none) {
            log.verbose("Starting retention task type=" + retentionMode);
            agent.IAgent_getCommonHeartbeat().addBehavior(
                    retentionMode == RetentionMode.zero ? new Zero() : new Last(),
                    EXPRESSION_TRACER + "#HeartBeat#" + metricName,
                    IntervalHeartbeat.kActive,
                    7500,
                    IntervalHeartbeat.kRunFirst
            );
        }

        log.info("Created: metricName=" + metricName + ", metricType=" + metricType + ", retentionMode=" + retentionMode + ", target=" + target.getClass().getName() + ", expression=" + expr.getExpression());
        log = new SimpleModuleFeedbackChannel(agent.IAgent_getModuleFeedback(), EXPRESSION_TRACER + "#" + metricName.replace(' ', '_'));
    }
    
    /**
     * Called before the instrumented method, but not used in this tracer.
     * @param i not used
     * @param data invocation
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
            hasReported.set(true);

            log.verbose("value=" + valueObject);
        } catch (Exception e) {
            log.verbose("Failed to evaluate expression", e);
        }
    }

    /**
     * Retention task that sends the value 0, in the absence of a real metric value.
     */
    class Zero implements ITimestampedRunnable {
        public void ITimestampedRunnable_execute(long l) {
            if (hasReported.getAndSet(false)) return;
            log.verbose("[Retention Heartbeat ZERO] value=0");
            recorder.add(0);
        }
    }

    /**
     * Retention task that sends the last recorded value, in the absence of a real metric value.
     * N.B. it's not the last averaged value.
     */
    class Last implements ITimestampedRunnable {
        public void ITimestampedRunnable_execute(long l) {
            if (hasReported.getAndSet(false)) return;
            Object last = recorder.getLast();
            log.verbose("[Retention Heartbeat LAST] value="+ last);
            recorder.add(last);
        }
    }
    

    /**
     * Returns the already created metric object.
     * @param metricName    name
     * @return metric
     */
    protected IDataAccumulator createDataAccumulator(String metricName) {
         return null;
    }

    protected Properties  loadExpressions(String filename, File agentDir) {
        File f = new File(filename);
        if (!f.isAbsolute()) {
            f = new File(agentDir, filename);
        }

        if (f.canRead()) {
            try {
                Properties p = new Properties();
                InputStream in = new FileInputStream(f);
                p.load(in);
                in.close();
                return p;
            } catch (Exception e) {
                log.warn("Failed to read config file: " + f, e);
            }
        }

        return new Properties();
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

    protected RetentionMode getParameterAsRetentionMode(String name, RetentionMode defaultValue) {
        String  value = this.getParameter(name);
        if (StringUtils.isEmpty(value)) return defaultValue;

        try {
            return RetentionMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            log.warn("Illegal value of tracer configuration parameter 'retain'. Got value '"+name+"'. Choose one of none|zero|last");
            return defaultValue;
        }
    }
    
    protected MetricType getParameterAsMetricType(String name, MetricType defaultValue) {
        String  value = this.getParameter(name);
        if (StringUtils.isEmpty(value)) return defaultValue;

        try {
            return MetricType.valueOf(value);
        } catch (IllegalArgumentException e) {
            log.warn("Illegal value of tracer configuration parameter 'metric'. Got value '"+name+"'. Choose one of average|counter|last|sum|text");
            return defaultValue;
        }
    }

}
