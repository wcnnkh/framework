package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.reflect.ReflectionUtils;
import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class InvokeOverloadMethodTest {

	@Test
	public void test() throws NoSuchMethodException {
		for (int i = 0; i < 100; i++) {
			int value = ReflectionUtils.invokeOverloadMethod(this, "a", true, 1, 2, 3, 4);
			assertTrue(value == 8);
		}

		for (int i = 0; i < 100; i++) {
			int value = ReflectionUtils.invokeOverloadMethod(this, "a", true, 1, 2, 3, UUIDSequences.global().next());
			assertTrue(value == 10);
		}
	}

	public int a(int a, Object b) {
		return 1;
	}

	public int a(int a, Object b, String c) {
		return 2;
	}

	public int a(int a, Object b, int c) {
		return 3;
	}

	public int a(int a, Object b, Object c) {
		return 4;
	}

	public int a(int a, int b, Object c) {
		return 5;
	}

	public int a(int a, int b, Object c, String d) {
		return 6;
	}

	public int a(int a, int b, Object c, int d) {
		return 7;
	}

	public int a(int a, int b, int c, int d) {
		return 8;
	}

	public int a(int a, int b, int c, int d, Object e) {
		return 9;
	}

	public int a(int a, int b, int c, String d) {
		return 10;
	}
}
