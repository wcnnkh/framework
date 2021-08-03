package scw.beans.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scw.beans.BeanFactory;
import scw.beans.ContextLoader;
import scw.beans.support.DefaultBeanFactory;

public class ContextLoaderTest {
	@Test
	public void test() throws Throwable {
		DefaultBeanFactory beanFactory = new DefaultBeanFactory();
		beanFactory.init();
		new Thread(() -> {
			BeanFactory current = ContextLoader.getCurrentBeanFactory();
			assertTrue(current != null);
			assertTrue(current == beanFactory);
		}).start();
	}
}
