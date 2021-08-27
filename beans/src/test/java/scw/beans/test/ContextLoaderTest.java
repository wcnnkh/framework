package scw.beans.test;

import static org.junit.Assert.assertTrue;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.ContextLoader;
import io.basc.framework.beans.support.DefaultBeanFactory;

import org.junit.Test;

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
