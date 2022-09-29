package io.basc.framework.context.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import io.basc.framework.context.ioc.annotation.Value;
import io.basc.framework.context.support.DefaultContext;
import io.basc.framework.orm.annotation.PrimaryKey;

public class IocTest {
	private static DefaultContext context = new DefaultContext();

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
		IocTest iocTest = context.getInstance(IocTest.class);
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
