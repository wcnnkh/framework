package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import io.basc.framework.aop.support.FieldSetterListenUtils;
import io.basc.framework.util.XUtils;

public class FieldSetterListenTest {
	@Test
	public void test() {
		A a = FieldSetterListenUtils.newInstance(A.class);
		a.setA(XUtils.getUUID());
		a.setB("hello");
		Map<String, Object> map = FieldSetterListenUtils.getChangeMap(a);
		System.out.println(map);
		assertTrue(map.size() == 2 && map.values().stream().allMatch((v) -> v == null));
		FieldSetterListenUtils.clearFieldSetterListen(a);
		String id = XUtils.getUUID();
		a.setA(id);
		map = FieldSetterListenUtils.getChangeMap(a);
		System.out.println(map);
		assertTrue(map.size() == 1);
	}

	public static class A {
		private String a;
		private Object b;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public Object getB() {
			return b;
		}

		public void setB(Object b) {
			this.b = b;
		}
	}
}
