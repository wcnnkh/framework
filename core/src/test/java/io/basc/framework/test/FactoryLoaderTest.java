package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Test;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryLoader;
import io.basc.framework.factory.support.DefaultBeanFactory;
import io.basc.framework.logger.LoggerFactory;

public class FactoryLoaderTest {
	@Test
	public void test() throws Throwable {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put(FactoryLoader.class.getName(), Level.ALL);
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
		BeanFactory current = FactoryLoader.getBeanFactory();
		assertTrue(current != null);
	}
}
