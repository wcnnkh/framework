package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.script.MathScriptEngine;

public class MathScriptEngineTest {
	private static MathScriptEngine engine = new MathScriptEngine();

	@Test
	public void test() {
		testNumberValue("1+1", 1 + 1);
		testNumberValue("100 - 2", 100 - 2);
		testNumberValue("11*200", 11 * 200);
		testNumberValue("22/2", 22 / 2);
	}

	private void testNumberValue(String script, double value) {
		assertTrue(engine.eval(script).toBigDecimal().doubleValue() == value);
	}
}
