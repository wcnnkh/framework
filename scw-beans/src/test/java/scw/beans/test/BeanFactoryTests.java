package scw.beans.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scw.beans.support.DefaultBeanFactory;
import scw.util.XUtils;

public class BeanFactoryTests {
	@Test
	public void test() throws Throwable {
		DefaultBeanFactory beanFactory = new DefaultBeanFactory();
		beanFactory.init();
		
		assertTrue(beanFactory.isInstance(TestFactoryBean1.class));
		assertTrue(beanFactory.isInstance(TestFactoryBean2.class));
		
		System.out.println(beanFactory.getInstance(TestFactoryBean2.class));
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
