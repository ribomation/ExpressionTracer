package com.ribomation.expression_tracer;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test of class ExpressionHolder.
 */
public class ExpressionHolder_Test extends TestCase {

    // ----------------------------------------------------------
    // Tests
    // ----------------------------------------------------------

    public void     test_getTargetIndex() {
        assertEquals("this", -1, target.getTargetIndex("this"));
        assertEquals("return", 0, target.getTargetIndex("$0"));
        assertEquals("arg-1", 1, target.getTargetIndex("$1"));
        assertEquals("arg-2", 2, target.getTargetIndex("$2"));

        try {
            target.getTargetIndex("");
            fail("Expected InvalidExpression");
        } catch (InvalidExpression e) {}

        try {
            target.getTargetIndex("THIS");
            fail("Expected InvalidExpression");
        } catch (InvalidExpression e) {}

        try {
            target.getTargetIndex("1");
            fail("Expected InvalidExpression");
        } catch (InvalidExpression e) {}

        try {
            target.getTargetIndex("x");
            fail("Expected InvalidExpression");
        } catch (InvalidExpression e) {}
    }

    public void     test_split_eq() {
        String[]  c = "foo=bar".split("\\s*=\\s*", 2);
        assertEquals("c.length", 2, c.length);
        assertEquals("foo", c[0]);
        assertEquals("bar", c[1]);

        c = "foo = bar".split("\\s*=\\s*", 2);
        assertEquals("c.length", 2, c.length);
        assertEquals("foo", c[0]);
        assertEquals("bar", c[1]);
    }

    public void     test_split_dot() {
        String[]  c = "foo.bar".split("\\s*\\.\\s*", 2);
        assertEquals("c.length", 2, c.length);
        assertEquals("foo", c[0]);
        assertEquals("bar", c[1]);

        c = "foo . bar".split("\\s*\\.\\s*", 2);
        assertEquals("c.length", 2, c.length);
        assertEquals("foo", c[0]);
        assertEquals("bar", c[1]);
    }

    public void     test_parse() {
        target = new ExpressionHolder("A|B|C:D=this.toString()");
        assertEquals("metricName", "A|B|C:D", target.getMetricName());
        assertEquals("targetIdx", -1, target.getTargetIdx());
        assertEquals("expression", "toString()", target.getExpression());
    }

    public void     test_parse2() {
        target = new ExpressionHolder("A|B|C:D=$0.getFoo().getBar()[2]");
        assertEquals("metricName", "A|B|C:D", target.getMetricName());
        assertEquals("targetIdx", 0, target.getTargetIdx());
        assertEquals("expression", "getFoo().getBar()[2]", target.getExpression());
    }

    public void     test_eval() {
        target = new ExpressionHolder("A:B=this.substring(3).charAt(0)");
        assertEquals("eval", "B", target.eval("FooBar").toString());
    }

    public void testAggregator_SUM() throws Exception {
        List<Integer> integers = Arrays.asList(10, 20, 30);
        String metric = "Root|Sub:Metric=this.( #_.SUM(#this) )";
        target = new ExpressionHolder(metric);
        assertEquals("eval", 60L, target.eval(integers));
    }
    
    public void testAggregator_PROD() throws Exception {
        List<Integer> integers = Arrays.asList(1,2,3,4,5);
        String metric = "Root|Sub:Metric=this.( #_.PROD(#this) )";
        target = new ExpressionHolder(metric);
        assertEquals("eval", 120L, target.eval(integers));
    }
    
    public void testAggregator_MIN() throws Exception {
        List<Integer> integers = Arrays.asList(10,20,5,30,40);
        String metric = "Root|Sub:Metric=this.( #_.MIN(#this) )";
        target = new ExpressionHolder(metric);
        assertEquals("eval", 5L, target.eval(integers));
    }
    
    public void testAggregator_MAX() throws Exception {
        List<Integer> integers = Arrays.asList(10,20,50,30,40);
        String metric = "Root|Sub:Metric=this.( #_.MAX(#this) )";
        target = new ExpressionHolder(metric);
        assertEquals("eval", 50L, target.eval(integers));
    }

    // ----------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    // Test Fixture
    // ----------------------------------------------------------
    private ExpressionHolder target;

    protected void setUp() {
        target = new ExpressionHolder();
    }

    protected void tearDown() {
        target = null;
    }

    public ExpressionHolder_Test(String name) {
        super(name);
    }
}
