package io.basc.framework.jackson.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.jackson.JacksonJSONSupport;
import io.basc.framework.util.XUtils;

public class JacksonTest {
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "a1");
		map.put("b", "b2");

		System.out.println(JacksonJSONSupport.INSTANCE.toJSONString(map));

		A a = new A(XUtils.getUUID());
		String json = JacksonJSONSupport.INSTANCE.toJSONString(a);
		System.out.println(json);
		a = JacksonJSONSupport.INSTANCE.parseObject(json, A.class);
		System.out.println(a);
		assertTrue(JacksonJSONSupport.INSTANCE.toJSONString(a).equals(json));
	}

	public static class A {
		private String a;
		private int b;
		private Object c;

		protected A() {
		}

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
