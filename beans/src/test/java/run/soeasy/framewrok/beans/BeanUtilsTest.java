package run.soeasy.framewrok.beans;

import java.util.UUID;

import org.junit.Test;

import lombok.Data;
import run.soeasy.framework.beans.BeanUtils;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;

public class BeanUtilsTest {
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

		BeanUtils.copyProperties(a, b);
		System.out.println(b);
		assert b.getValue() == null;

		b.setValue(UUID.randomUUID().toString());
		a = new A();
		BeanUtils.copyProperties(b, a);
		System.out.println(a);
		assert a.getValue() != null;
	}
	
	@Test
	public void test2() {
		B b = new B();
		b.setName("bean");
		
		A a = new A();
		a.setName("ignore");
		a.setValue(UUID.randomUUID().toString());

		
		BeanUtils.copyProperties(b, a, PropertyMappingFilter.IGNORE_NULL);
		System.out.println(a);
		assert a.getValue() != null;
	}
}
