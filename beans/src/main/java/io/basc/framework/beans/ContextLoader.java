package io.basc.framework.beans;

import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过classloader来获取上下文的BeanFactory, 如果一个classloader对应两个beanFactory那么只会有一个生效
 * 
 * @see DefaultBeanFactory#init()
 * @author shuchaowen
 *
 */
public class ContextLoader {
	private static volatile Map<ClassLoader, BeanFactory> CONTEXT_MAP = new ConcurrentHashMap<>(4);
	private static ThreadLocal<BeanFactory> LOCAL_BEANFACTORY = new NamedThreadLocal<>(ContextLoader.class.getName());

	public static void bindBeanFactory(BeanFactory beanFactory) {
		BeanFactory oldBeanFactory = CONTEXT_MAP.putIfAbsent(beanFactory.getClassLoader(), beanFactory);
		if (oldBeanFactory != null) {
			bindThreadBeanFactory(beanFactory);
		}
	}

	public static void bindThreadBeanFactory(BeanFactory beanFactory) {
		LOCAL_BEANFACTORY.set(beanFactory);
	}

	@Nullable
	public static BeanFactory getBeanFactory(ClassLoader classLoader) {
		return CONTEXT_MAP.get(classLoader);
	}

	@Nullable
	public static BeanFactory getCurrentBeanFactory() {
		BeanFactory beanFactory = LOCAL_BEANFACTORY.get();
		if (beanFactory == null) {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			beanFactory = getBeanFactory(classLoader);
		}
		return beanFactory;
	}
}
