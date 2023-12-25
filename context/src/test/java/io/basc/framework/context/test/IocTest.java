package io.basc.framework.context.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.component.Component;
import io.basc.framework.beans.factory.component.Value;
import io.basc.framework.context.support.DefaultApplicationContext;
import io.basc.framework.orm.annotation.PrimaryKey;

@Component
public class IocTest {
	private static DefaultApplicationContext context = new DefaultApplicationContext(Scope.DEFAULT);

	static {
		try {
			context.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	} 

	@Value(value = "test.xml")
	private Map<String, TestBean> map;

	@Test
	public void test() {
		IocTest iocTest = context.getBean(IocTest.class);
		System.out.println(iocTest.map);
		assertTrue(iocTest.map.size() == 3);
	}

	public static class TestBean {
		@PrimaryKey
		private String id;
		private String name;

		@Override
		public String toString() {
			return "id=" + id + ", name=" + name;
		}
	}
}
