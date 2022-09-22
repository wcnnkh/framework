package io.basc.framework.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.XUtils;

/**
 * 通过classloader来获取上下文的BeanFactory, 如果一个classloader对应两个beanFactory那么只会有一个生效
 * 
 * @see DefaultBeanFactory#init()
 * @author shuchaowen
 *
 */
public class FactoryLoader {
	private static Logger logger = LoggerFactory.getLogger(FactoryLoader.class);
	private static volatile Map<ClassLoader, BeanFactory> CONTEXT_MAP = new ConcurrentHashMap<>(4);
	private static ThreadLocal<BeanFactory> LOCAL_BEANFACTORY = new NamedThreadLocal<>(FactoryLoader.class.getName());

	public static void bindBeanFactory(BeanFactory beanFactory) {
		BeanFactory oldBeanFactory = CONTEXT_MAP.putIfAbsent(beanFactory.getClassLoader(), beanFactory);
		if (oldBeanFactory != null) {
			bindThreadBeanFactory(beanFactory);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Bind factory[{}] in classloader[{}]", beanFactory, beanFactory.getClassLoader());
			}
		}
	}

	public static void bindThreadBeanFactory(BeanFactory beanFactory) {
		if (logger.isDebugEnabled()) {
			logger.debug("Bind factory[{}] in thread[{}]", beanFactory, Thread.currentThread().getName());
		}
		LOCAL_BEANFACTORY.set(beanFactory);
	}

	@Nullable
	public static BeanFactory getBeanFactory(ClassLoader classLoader) {
		ClassLoader use = classLoader;
		BeanFactory beanFactory = null;
		String traceId = null;
		while (beanFactory == null) {
			if (use == null) {
				break;
			}

			if (logger.isTraceEnabled()) {
				if (traceId == null) {
					traceId = XUtils.getUUID();
				}
				logger.trace("Try [{}] find factory in classloader[{}]", traceId, use);
			}

			beanFactory = CONTEXT_MAP.get(use);
			if (beanFactory == null) {
				use = classLoader.getParent();
			}
		}
		return beanFactory;
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
