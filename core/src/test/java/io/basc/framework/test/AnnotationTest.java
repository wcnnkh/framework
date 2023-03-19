package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.lang.Nullable;

@Nullable
public class AnnotationTest {
	@Test
	public void test() {
		for (int i = 0; i < 1000; i++) {
			test1();
			test2();
		}

		long t = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			test2();
		}
		t = System.currentTimeMillis() - t;
		System.out.println("test2用时：" + t + "ms");

		t = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			test1();
		}
		t = System.currentTimeMillis() - t;
		System.out.println("test1用时：" + t + "ms");
	}

	private void test1() {
		assertTrue(AnnotationTest.class.isAnnotationPresent(Nullable.class));
	}

	private void test2() {
		assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotationTest.class, Nullable.class));
	}
}
