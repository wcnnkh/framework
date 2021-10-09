package io.basc.framework.beans.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import io.basc.framework.beans.annotation.Value;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.orm.annotation.PrimaryKey;

public class IocTest {
	private static DefaultBeanFactory beanFactory = new DefaultBeanFactory();

	static {
		try {
			beanFactory.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Value(value = "test.xml")
	private Map<String, TestBean> map;

	@Test
	public void test() {
		IocTest iocTest = beanFactory.getInstance(IocTest.class);
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
