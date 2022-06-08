package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.XUtils;

public class ToStringTest {
	@Test
	public void test() {
		ToStringBean bean = new ToStringBean();
		bean.setA(new int[] { 1, 2 });
		bean.setB(XUtils.getUUID());
		bean.setC(new int[][] { { 1, 2 }, { 1, 2 } });
		System.out.println(bean);
	}

	public static class ToStringBean {
		private int[] a;
		private String b;
		private int[][] c;

		public int[][] getC() {
			return c;
		}

		public void setC(int[][] c) {
			this.c = c;
		}

		public int[] getA() {
			return a;
		}

		public void setA(int[] a) {
			this.a = a;
		}

		public String getB() {
			return b;
		}

		public void setB(String b) {
			this.b = b;
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
