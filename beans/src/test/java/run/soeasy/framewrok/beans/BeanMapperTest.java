package run.soeasy.framewrok.beans;

import java.util.UUID;

import org.junit.Test;

import lombok.Data;
import run.soeasy.framework.beans.BeanMapper;

public class BeanMapperTest {
	@Data
	private static class A {
		private String name;
		private Object value;
	}

	@Data
	public static class B {
		private String name;
		private String value;
	}

	@Test
	public void test() {
		A a = new A();
		a.setName("bean");
		a.setValue(UUID.randomUUID().toString());

		B b = new B();

		BeanMapper.copyProperties(a, b);
		System.out.println(b);
		assert b.getValue() == null;

		b.setValue(UUID.randomUUID().toString());
		a = new A();
		BeanMapper.copyProperties(b, a);
		System.out.println(a);
		assert a.getValue() != null;
	}
}
