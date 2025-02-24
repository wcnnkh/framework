package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class CloneableTest {
	@Test
	public void test() throws CloneNotSupportedException {
		A a = new A(UUIDSequences.global().next());
		A b = a.clone();
		assertTrue(StringUtils.equals(a.a, b.a));
	}

	private static class A implements Cloneable {
		private String a;

		public A(String a) {
			this.a = a;
		}

		@Override
		public A clone() throws CloneNotSupportedException {
			return (A) super.clone();
		}
	}
}
