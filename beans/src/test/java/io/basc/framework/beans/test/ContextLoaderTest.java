package io.basc.framework.beans.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Test;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.ContextLoader;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.logger.LoggerFactory;

public class ContextLoaderTest {
	@Test
	public void test() throws Throwable {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put(ContextLoader.class.getName(), Level.ALL);
		DefaultBeanFactory beanFactory = new DefaultBeanFactory();
		beanFactory.init();
		test(beanFactory);
		CountDownLatch countDownLatch = new CountDownLatch(1);
		new Thread(() -> {
			try {
				test(beanFactory);
			} finally {
				countDownLatch.countDown();
			}
		}).start();
		countDownLatch.await();
	}
	
	private void test(BeanFactory beanFactory) {
		BeanFactory current = ContextLoader.getCurrentBeanFactory();
		assertTrue(current != null);
	}
}
