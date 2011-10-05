package com.wily.introscope.agent;

import java.io.PrintStream;
import java.security.ProtectionDomain;

import com.wily.introscope.agent.blame.ComponentTracer;
import com.wily.introscope.agent.blamestackfeature.IBlameStackFeatureAdministrator;
import com.wily.introscope.agent.connection.IsengardServerConnectionManager;
import com.wily.introscope.agent.enterprise.IServerConnectionManager;
import com.wily.introscope.agent.environment.EnvironmentAdministrator;
import com.wily.introscope.agent.event.EventAdministrator;
import com.wily.introscope.agent.extension.ExtensionAdministrator;
import com.wily.introscope.agent.recording.MetricRecordingAdministrator;
import com.wily.introscope.agent.service.ServiceAdministrator;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.trace.IMethodTracer;
import com.wily.introscope.agent.trace.IParameterizedMethodTracer;
import com.wily.introscope.agent.trace.TracerAdministrator;
import com.wily.introscope.agent.transactiontrace.ExtensionsTraceController;
import com.wily.introscope.agent.transactiontrace.IPlatformCrossProcessDataHelper;
import com.wily.introscope.agent.transactiontrace.TransactionTraceController;
import com.wily.introscope.agent.transformer.TransformerAdministrator;
import com.wily.introscope.spec.metric.AgentMetric;
import com.wily.introscope.spec.server.transactiontrace.TransactionComponentData;
import com.wily.introscope.stat.timeslice.ATimeslicedValue;
import com.wily.util.IConfigurationListener;
import com.wily.util.INameChangeListener;
import com.wily.util.classfile.IModeledClass;
import com.wily.util.classfile.IModeledMethod;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.heartbeat.IntervalHeartbeat;
import com.wily.util.properties.IndexedProperties;
import com.wily.util.resource.IResource;
import com.wily.util.task.IExecutionQueue;
import com.wily.util.text.IStringLocalizer;

/**
 * Empty IAgent.
 */
public class MockAgent implements IAgent {
    private IModuleFeedbackChannel  log = new MockModuleFeedback();

    public MockAgent() {
    }

    public MockAgent(IModuleFeedbackChannel log) {
        this.log = log;
    }

    public IsengardServerConnectionManager IAgent_getIsengardServerConnection() {
        return null;
    }

    public String IAgent_getName() {
        return null;
    }

    public boolean IAgent_isIsengardEnabled() {
        return false;
    }

    public void IAgent_queueEvent(TransactionComponentData transactionComponentData) {
    }

    public boolean debug_isRetrievingValues() {
        return false;
    }

//   public ATimeslicedValue[] debug_retrieveCurrentValues(ESEAttribute eseAttribute) {
//       return new ATimeslicedValue[0];
//   }

   public void debug_setRetrievingValues(boolean b) {
   }

   public void IAgent_addNameChangeListener(INameChangeListener iNameChangeListener) {
   }

   public IntervalHeartbeat IAgent_getCommonHeartbeat() {
       return null;
   }

   public ComponentTracer IAgent_getComponentTracer() {
       return null;
   }

   public IResource IAgent_getConfigurationResource() {
       return null;
   }

   public DataAccumulatorFactory IAgent_getDataAccumulatorFactory() {
       return null;
   }

   public EnvironmentAdministrator IAgent_getEnvironmentAdministrator() {
       return null;
   }

   public EventAdministrator IAgent_getEventAdministrator() {
       return null;
   }

   public ExtensionAdministrator IAgent_getExtensionAdministrator() {
       return null;
   }

   public IndexedProperties IAgent_getIndexedProperties() {
       return new IndexedProperties();
   }

   public MetricRecordingAdministrator IAgent_getMetricRecordingAdministrator() {
       return null;
   }

   public IModuleFeedbackChannel IAgent_getModuleFeedback() {
       return log;
   }

   public ServiceAdministrator IAgent_getServiceAdministrator() {
       return null;
   }

   public IExecutionQueue IAgent_getSharedAsyncQueue() {
       return null;
   }

   public PrintStream IAgent_getStreamReplacement(int i) {
       return null;
   }

   public IStringLocalizer IAgent_getStringLocalizer() {
       return null;
   }

   public TracerAdministrator IAgent_getTracerAdministrator() {
       return null;
   }

   public TransformerAdministrator IAgent_getTransformerAdministrator() {
       return null;
   }

//   public boolean IAgent_isMetricShutOff(ESEAttribute eseAttribute) {
//       return false;
//   }

   public IParameterizedMethodTracer IAgent_loadParameterizedTracer(int i, String string, String string1, String string2, Object object, String string3) {
       return null;
   }

   public IMethodTracer IAgent_loadTracer(int i, String string, String string1, String string2, Object object, String string3) {
       return null;
   }

   public void IAgent_noticeEvent(String string, String string1, String string2, String string3, String string4, Object object, String string5, String string6) {
   }

   public void IAgent_safeReportError(String string, Throwable throwable) {
   }

   public void IAgent_safeReportWarning(String string, Throwable throwable) {
   }

   public void IAgent_setRecordingEnabled(boolean b) {
   }

   public byte[] IAgent_transformClass(boolean b, ClassLoader classLoader, String string, Class aClass, ProtectionDomain protectionDomain, byte[] bytes) {
       return new byte[0];
   }

   public void IAgent_writeDebugInfo(IModuleFeedbackChannel iModuleFeedbackChannel) {
   }

    public TransactionTraceController IAgent_getTransactionTraceController() {
        return null;
    }

    public ExtensionsTraceController IAgent_getExtensionsTraceController() {
        return null;
    }

    public IntervalHeartbeat IAgent_getConfigHeartbeat() {
        return null;
    }

    public void IAgent_transformAssembly(int i, String s) {
    }

    public byte[] IAgent_transformMethod(IModeledClass iModeledClass, IModeledMethod iModeledMethod, byte[] bytes, Object o) {
        return new byte[0];
    }

    public boolean IAgent_isMetricShutOff(AgentMetric agentMetric) {
        return false;
    }

    public void IAgent_safeReportInfo(String s, Throwable throwable) {
    }

    public void IAgent_removeNameChangeListener(INameChangeListener iNameChangeListener) {
    }

    public void IAgent_addNegotiatedNameChangeListener(INameChangeListener iNameChangeListener) {
    }

    public void IAgent_removeNegotiatedNameChangeListener(INameChangeListener iNameChangeListener) {
    }

    public Object substituteResult(Object o, Object o1, String s) {
        return null;
    }

    public String[] IAgent_getHostProcessAgentTriplet() {
        return new String[0];
    }

    public void addCrossProcessDataHelper(IPlatformCrossProcessDataHelper iPlatformCrossProcessDataHelper) {
    }

    public IBlameStackFeatureAdministrator getBlameStackFeatureAdministrator() {
        return null;
    }

    public void setNewTransport(IServerConnectionManager iServerConnectionManager) {
    }

    public void addConfigurationListener(IConfigurationListener iConfigurationListener) {
    }

    public ATimeslicedValue[] debug_retrieveCurrentValues(AgentMetric agentMetric) {
        return new ATimeslicedValue[0];
    }

    public void addEventListener(IEventListener iEventListener) {
    }

    public boolean isTestAgent() {
        return false;
    }
}
