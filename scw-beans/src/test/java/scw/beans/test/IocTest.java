package scw.beans.test;

import io.basc.framework.beans.annotation.Value;
import io.basc.framework.beans.ioc.value.ResourceValueProcesser;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.orm.annotation.PrimaryKey;

import java.util.Map;

import org.junit.Test;

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
