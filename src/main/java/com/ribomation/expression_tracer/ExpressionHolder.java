package com.ribomation.expression_tracer;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.DefaultMemberAccess;

import com.wily.introscope.agent.trace.InvocationData;

import java.util.Map;
import java.util.Properties;

/**
 * Helper class that encapsulates the invocation expression and its dependencies.
 * If first extracts out the real metric name part and the invocation expression,
 * and identifies the invocation target. An equals sign ('=') separates the metric
 * name from the invocation expression.
 * <p>
 * The invocation expression must start with an invocation target followed by a
 * dot ('.') and then a valid <a href="http://www.ognl.org/">OGNL</a>
 expression operating on the target.
 * <p>
 * The invocation target can be either of
 * <ul>
 *   <li>this - the intrumented object itself</li>
 *   <li>$1, $2, ... - an argument object of the instrumented method</li>
 *   <li>$0 - the returned value from the method</li>
 * </ul>
 */
public class ExpressionHolder {
    private static final String     METRIC_SEP = "\\s*=\\s*";
    private static final String     TARGET_SEP = "\\s*\\.\\s*";
    private static final String     THIS       = "this";
    private static final String     ARG_PREFIX = "$";
    private static final int        THIS_IDX   = -1;
    private static final int        RETURN_IDX = 0;
    private static final int        THROW_IDX  = -2;

    private String                  metricName;
    private String                  expression;
    private int                     targetIdx;
    private Object                  ognlExpr;
    private Properties              externalExpression = new Properties();

    /**
     * Do nothing constructor for unit testing.
     */
    protected ExpressionHolder() {
    }

    public ExpressionHolder(String metricName, Properties cfg) {
        this.metricName = metricName;
        this.externalExpression = cfg;

        // name=expr
        String[]    comps = metricName.split(METRIC_SEP, 2);
        if (comps.length != 2) throw new InvalidExpression(metricName);
        this.metricName = comps[0];
        this.expression = comps[1];

        if (this.expression.startsWith("@")) {
            String exprName = this.expression.substring(1);
            String expr = externalExpression.getProperty(exprName);
            if (expr != null) {
                this.expression = expr;
            }
        }

        // $0.getValue()
        comps = this.expression.split(TARGET_SEP, 2);
        if (comps.length != 2 && comps.length != 1) throw new InvalidExpression(metricName);
        this.targetIdx  = getTargetIndex(comps[0]);
        this.expression = (comps.length == 2 ? comps[1] : "toString()");

        try {
            this.ognlExpr = Ognl.parseExpression(this.expression);
        } catch (OgnlException e) {
            throw new InvalidExpression(metricName, e);
        }
    }

    /**
     * Parses the given metric name into the name part, target part and expression part.
     * <br/>
     * <UL>
     * <LI><code>"Resource|Path:Metric=$1.getValue()"</code> -&gt; {Resource|Path:Metric, 1, getValue()}</LI>
     * <LI><code>"Resource|Path:Metric=$1"</code> -&gt; {Resource|Path:Metric, 1, toString()}</LI>
     * <LI><code>"Resource|Path:Metric=this.getValue()"</code> -&gt; {Resource|Path:Metric, this, getValue()}</LI>
     * </UL>
     *
     * @param metricName    PBD metric name
     */
    public ExpressionHolder(String metricName) {
        this(metricName, new Properties());
    }

    /**
     * Evaluates the invocation. This is a convenience method, that first selects the target object
     * and then evaluates it using the expression.
     * @param data   invocation context
     * @return result
     */
    public Object       eval(InvocationData data) {
        return eval( getTarget(data) );
    }

    /**
     * Selects the target object (this, argument or returnValue).
     * @param data      invocation context
     * @return target object
     */
    public Object       getTarget(InvocationData data) {
        if (targetIdx == THIS_IDX) {
            return data.getInvocationObject();
        } else if (targetIdx == RETURN_IDX) {
            return data.getInvocationReturnValueAsObject();
        } else {
            return data.getInvocationParameterAsObject(targetIdx - 1);
        }
    }

    /**
     * Evaluates the expression with the given object as the root.
     * @param target    root object
     * @return result
     */
    public Object       eval(Object target) {
        if (target == null) return null;

        try {
            Map ctx = Ognl.createDefaultContext(target);
            Ognl.setMemberAccess(ctx, new DefaultMemberAccess(true));

            return Ognl.getValue(ognlExpr, ctx, target);
        } catch (OgnlException e) {
            throw new FailedEvaluation(metricName, expression, target, e);
        }
    }

    /**
     * Extracts the numerical target index from the given string.
     * @param target    target string ('this', '$0', '$1', ...)
     * @return target index (-1, 0, 1, ...)
     */
    protected int   getTargetIndex(String target) {
        if (THIS.equals(target)) return THIS_IDX;

        if (target.startsWith(ARG_PREFIX)) {
            try {
                return Integer.parseInt( target.substring(ARG_PREFIX.length()) );
            } catch (Exception e) {
                throw new InvalidExpression(target, e);
            }
        }

        throw new InvalidExpression(target);
    }

    /**
     * Returns the expression
     * @return expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns the extracted metric name
     * @return metric name
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Returns the target index
     * @return target index (-1, 0, 1, ...)
     */
    public int getTargetIdx() {
        return targetIdx;
    }

    public String toString() {
        return "ExpressionHolder{" +
                "metricName='" + metricName + '\'' +
                ", expression='" + expression + '\'' +
                ", targetIdx=" + targetIdx +
                '}';
    }
}
