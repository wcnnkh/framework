package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Test;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.support.DefaultBeanFactory;
import io.basc.framework.beans.factory.support.FactoryLoader;
import io.basc.framework.logger.LoggerFactory;

public class FactoryLoaderTest {
	@Test
	public void test() throws Throwable {
		LoggerFactory.getSource().getLevelManager().getMaster().put(FactoryLoader.class.getName(), Level.ALL);
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
