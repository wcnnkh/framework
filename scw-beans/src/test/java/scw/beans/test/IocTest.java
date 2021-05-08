package scw.beans.test;

import java.util.Map;

import org.junit.Test;

import scw.beans.annotation.Value;
import scw.beans.ioc.value.ResourceValueProcesser;
import scw.beans.support.DefaultBeanFactory;
import scw.orm.annotation.PrimaryKey;

public class IocTest {
	private static DefaultBeanFactory beanFactory = new DefaultBeanFactory();

	static {
		try {
			beanFactory.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Value(value = "test.xml", processer = ResourceValueProcesser.class)
	private Map<String, TestBean> map;

	@Test
	public void test() {
		IocTest iocTest = beanFactory.getInstance(IocTest.class);
		System.err.println(iocTest.map);
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
