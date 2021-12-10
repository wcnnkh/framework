package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.env.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(ExecutorService.class.getName())) {
			beanFactory.registerDefinition(ExecutorService.class.getName(),
					new ThreadPoolExecutorBeanDefinition(beanFactory));
		}

		if (!beanFactory.containsDefinition(ScheduledExecutorService.class.getName())) {
			beanFactory.registerDefinition(ScheduledExecutorService.class.getName(),
					new ScheduledExecutorServiceBeanDefinition(beanFactory));
		}
	}

	private static int getCorePoolSize(Environment environment) {
		return environment.getValue("executor.pool.core.size", int.class, 0);
	}

	private static int getMaxmumPoolSize(Environment environment) {
		return environment.getValue("executor.pool.max.size", int.class, Runtime.getRuntime().availableProcessors());
	}

	private static long getKeepAliveTime(Environment environment) {
		return environment.getValue("executor.pool.keepAliveTime", long.class, 1L);
	}

	private static TimeUnit getTimeUnit(Environment environment) {
		return environment.getValue("executor.pool.keepAliveTime.unit", TimeUnit.class, TimeUnit.HOURS);
	}

	private final class ScheduledExecutorServiceBeanDefinition extends DefaultBeanDefinition {

		public ScheduledExecutorServiceBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, ScheduledThreadPoolExecutor.class);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws BeansException {
			return Executors.newScheduledThreadPool(getCorePoolSize(beanFactory.getEnvironment()));
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			super.destroy(instance);
			if (instance instanceof ScheduledExecutorService) {
				((ScheduledExecutorService) instance).shutdownNow();
			}
		}
	}

	private final class ThreadPoolExecutorBeanDefinition extends DefaultBeanDefinition {
		public ThreadPoolExecutorBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, ThreadPoolExecutor.class);
		}

		@Override
		public boolean isInstance() {
			return true;
		}

		@Override
		public Object create() throws BeansException {
			return new ThreadPoolExecutor(getCorePoolSize(beanFactory.getEnvironment()),
					getMaxmumPoolSize(beanFactory.getEnvironment()), getKeepAliveTime(beanFactory.getEnvironment()),
					getTimeUnit(beanFactory.getEnvironment()), new LinkedBlockingQueue<Runnable>());
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			if (instance instanceof ThreadPoolExecutor) {
				((ThreadPoolExecutor) instance).shutdownNow();
			}
			super.destroy(instance);
		}
	}
}
