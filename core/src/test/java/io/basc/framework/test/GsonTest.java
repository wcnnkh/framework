package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.gson.GsonSupport;
import io.basc.framework.util.XUtils;

public class GsonTest {
	@Test
	public void test() {
		A a = new A(XUtils.getUUID());
		String json = GsonSupport.INSTANCE.toJsonString(a);
		System.out.println(json);
		a = GsonSupport.INSTANCE.parseObject(json, A.class);
		System.out.println(a);
		assertTrue(GsonSupport.INSTANCE.toJsonString(a).equals(json));
	}

	public static class A {
		private String a;
		private int b;
		private Object c;

		public A(String a) {
			this.a = a;
		}

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public Object getC() {
			return c;
		}

		public void setC(Object c) {
			this.c = c;
		}
	}
}
