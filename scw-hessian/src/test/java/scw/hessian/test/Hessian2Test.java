package scw.hessian.test;

import java.io.Serializable;

import org.junit.Test;

import scw.hessian.Hessian2Serializer;

public class Hessian2Test {
	@Test
	public void test() throws ClassNotFoundException {
		Hessian2Serializer serializer = new Hessian2Serializer();
		TestBean testBean = new TestBean();
		testBean.setA("testa");
		testBean.setB(1);

		System.out.println("before:" + testBean);
		byte[] data = serializer.serialize(testBean);
		testBean = serializer.deserialize(data);
		System.out.println("after:" + testBean);
	}

	public static final class TestBean implements Serializable {
		private static final long serialVersionUID = 1L;
		private String a;
		private int b;

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

		@Override
		public String toString() {
			return "a=" + a + ", b=" + b;
		}
	}
}
