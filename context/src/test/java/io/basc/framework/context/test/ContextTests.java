package io.basc.framework.context.test;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.context.support.DefaultApplicationContext;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.orm.annotation.ConfigurationProperties;
import io.basc.framework.util.XUtils;

@SuppressWarnings("unused")
public class ContextTests {
	private static DefaultApplicationContext environment = new DefaultApplicationContext(Scope.DEFAULT);

	static {
		environment.init();
	}

	@Test
	public void configurableProperties() {
		environment.getProperties().put("test.a", "abc");
		environment.getProperties().put("test.b", "121");

		TestConfigurableBean bean = environment.getBean(TestConfigurableBean.class);
		System.out.println(JsonUtils.getSupport().toJsonString(bean));

		TestPropertiesBean propertiesBean = environment.getBean(TestPropertiesBean.class);
		System.out.println(propertiesBean);
	}

	@ConfigurationProperties(prefix = "test")
	private static class TestPropertiesBean extends Properties {
		private static final long serialVersionUID = 1L;
	}

	@ConfigurationProperties("test")
	private static class TestConfigurableBean {
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
	}

	public static class TestFactoryBean1 {
		private final String name;

		public TestFactoryBean1() {
			this(XUtils.getUUID());
		}

		public TestFactoryBean1(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static class TestFactoryBean2 {
		private final TestFactoryBean1 bean1;

		public TestFactoryBean2(TestFactoryBean1 bean1) {
			this.bean1 = bean1;
		}

		public TestFactoryBean1 getBean1() {
			return bean1;
		}

		@Override
		public String toString() {
			return bean1.toString();
		}
	}
}
