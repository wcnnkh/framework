package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.script.MathScriptEngine;
import io.basc.framework.script.ScriptException;

public class MathScriptEngineTest {
	private static MathScriptEngine engine = new MathScriptEngine();

	@Test
	public void test() {
		testNumberValue("+1234", 1234);
		testNumberValue("-1234", -1234);
		testNumberValue("1+1", 1 + 1);
		testNumberValue("100 - 2", 100 - 2);
		testNumberValue("11*200", 11 * 200);
		testNumberValue("22/2", 22 / 2);
		testNumberValue("4-(1+ (3 -1)) + (0)", 1);

		try {
			testNumberValue("1,2,3,4", 1234);
			// 无法解析, 应该出现异常
			throw new NullPointerException();
		} catch (ScriptException e) {
			// ignore
		}
	}

	private void testNumberValue(String script, double value) {
		assertTrue(engine.eval(script).toBigDecimal().doubleValue() == value);
	}
}
