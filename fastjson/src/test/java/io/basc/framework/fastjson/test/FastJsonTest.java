package io.basc.framework.fastjson.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.fastjson.FastJsonSupport;
import io.basc.framework.util.XUtils;

public class FastJsonTest {
	@Test
	public void test() {
		A a = new A(XUtils.getUUID());
		String json = FastJsonSupport.INSTANCE.toJsonString(a);
		System.out.println(json);
		a = FastJsonSupport.INSTANCE.parseObject(json, A.class);
		System.out.println(a);
		assertTrue(FastJsonSupport.INSTANCE.toJsonString(a).equals(json));
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
