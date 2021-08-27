package scw.beans.test;

import static org.junit.Assert.assertTrue;
import io.basc.framework.beans.annotation.ConfigurationProperties;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.util.XUtils;

import java.util.Properties;

import org.junit.Test;

@SuppressWarnings("unused")
public class BeanFactoryTests {
	private static DefaultBeanFactory beanFactory = new DefaultBeanFactory();

	static {
		try {
			beanFactory.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Throwable {
		assertTrue(beanFactory.isInstance(TestFactoryBean1.class));
		assertTrue(beanFactory.isInstance(TestFactoryBean2.class));
		System.out.println(beanFactory.getInstance(TestFactoryBean2.class));
	}

	@Test
	public void configurableProperties() {
		beanFactory.getEnvironment().put("test.a", "abc");
		beanFactory.getEnvironment().put("test.b", "121");

		TestConfigurableBean bean = beanFactory.getInstance(TestConfigurableBean.class);
		System.out.println(JSONUtils.getJsonSupport().toJSONString(bean));

		TestPropertiesBean propertiesBean = beanFactory.getInstance(TestPropertiesBean.class);
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
