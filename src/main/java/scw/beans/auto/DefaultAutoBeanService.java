package scw.beans.auto;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executor;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.annotation.Host;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.DefaultExecutor;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.logger.LazyLogger;
import scw.logger.Logger;
import scw.result.ResultFactory;
import scw.rpc.http.HttpRestfulRpcProxy;

public final class DefaultAutoBeanService implements AutoBeanService {
	private static Logger logger = new LazyLogger(DefaultAutoBeanService.class);

	private AutoBean defaultService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoBean autoBean = null;
		if (clazz == ResultFactory.class) {
			autoBean = createResultFactory(beanFactory, propertyFactory);
		} else if (clazz == Executor.class) {
			autoBean = new SimpleAutoBean(beanFactory, DefaultExecutor.class, propertyFactory);
		}

		if (autoBean != null) {
			return autoBean;
		}

		// 未注解service时接口默认实现
		if (clazz.isInterface()) {
			String name = clazz.getName() + "Impl";
			if (ClassUtils.isExist(name) && beanFactory.isInstance(name)) {
				logger.info("{} reference {}", clazz.getName(), name);
				return new ReferenceAutoBean(beanFactory, name);
			} else {
				int index = clazz.getName().lastIndexOf(".");
				name = index == -1 ? (clazz.getName() + "Impl")
						: (clazz.getName().substring(0, index) + ".impl." + clazz.getSimpleName() + "Impl");
				if (ClassUtils.isExist(name) && beanFactory.isInstance(name)) {
					logger.info("{} reference {}", clazz.getName(), name);
					return new ReferenceAutoBean(beanFactory, name);
				}
			}
		}

		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			// Host注解
			Host host = clazz.getAnnotation(Host.class);
			if (host != null) {
				String proxyName = propertyFactory.getProperty("rpc.http.host.proxy");
				if (StringUtils.isEmpty(proxyName)) {
					proxyName = HttpRestfulRpcProxy.class.getName();
				}

				if (beanFactory.isInstance(proxyName)) {
					return new ProxyAutoBean(beanFactory, clazz, proxyName);
				}
			}
		}

		if (!ReflectUtils.isInstance(clazz, false)) {
			return serviceChain.service(clazz, beanFactory, propertyFactory);
		}

		return new SimpleAutoBean(beanFactory, clazz, propertyFactory);
	}

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoImpl autoConfig = clazz.getAnnotation(AutoImpl.class);
		if (autoConfig == null) {
			return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
		}

		Collection<Class<?>> implList = getAutoImplClass(autoConfig, clazz, propertyFactory);
		if (CollectionUtils.isEmpty(implList)) {
			return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
		}

		for (Class<?> clz : implList) {
			AutoBean autoBean = AutoBeanUtils.autoBeanService(clz, autoConfig, beanFactory, propertyFactory);
			if (autoBean != null && autoBean.isInstance()) {
				return autoBean;
			}
		}

		return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
	}

	private static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig, Class<?> type,
			PropertyFactory propertyFactory) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.className()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = FormatUtils.format(name, propertyFactory, true);
			Class<?> clz = null;
			try {
				clz = ClassUtils.forName(name);
			} catch (ClassNotFoundException e) {
			}

			if (clz == null) {
				continue;
			}

			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from name {}", type, clz);
			}
		}

		for (Class<?> clz : autoConfig.value()) {
			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from {}", type, clz);
			}
		}
		return list;
	}

	private AutoBean createResultFactory(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		String resultPropertiesFile = propertyFactory.getProperty("result.config");
		String charsetName = propertyFactory.getProperty("result.charset");
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Constants.DEFAULT_CHARSET_NAME;
		}

		int defaultErrorCode = StringUtils.parseInt(propertyFactory.getProperty("result.error.code"), 1);
		int defaultSuccessCode = StringUtils.parseInt(propertyFactory.getProperty("result.success.code"), 0);
		int loginExpiredCode = StringUtils.parseInt(propertyFactory.getProperty("result.expired.code"), -1);
		int parameterErrorCode = StringUtils.parseInt(propertyFactory.getProperty("result.parameter.error.code"), 2);
		String contentType = propertyFactory.getProperty("result.contentType");
		if (StringUtils.isEmpty(contentType)) {
			contentType = "application/json";
		}

		boolean defaultRollbackOnly = StringUtils.parseBoolean(propertyFactory.getProperty("result.rollbackOnly"),
				true);
		if (ClassUtils.isExist("javax.servlet.Servlet")) {
			Object[] args = new Object[] { resultPropertiesFile, charsetName, defaultErrorCode, defaultSuccessCode,
					loginExpiredCode, parameterErrorCode, contentType, defaultRollbackOnly };
			logger.info(
					"ServletResultFactory: propertiesFile={}, charsetName={}, defaultErrorCode={}, defaultSuccessCode={}, loginExpiredcode={}, parameterErrorCode={}, contentType={}, defaultRollbackOnly={}",
					args);
			return new DefaultAutoBean(beanFactory, "scw.result.servlet.ServletResultFactory",
					new Class<?>[] { String.class, String.class, int.class, int.class, int.class, int.class,
							String.class, boolean.class },
					args);
		} else {
			Object[] args = new Object[] { resultPropertiesFile, charsetName, defaultErrorCode, defaultSuccessCode,
					loginExpiredCode, parameterErrorCode, defaultRollbackOnly };
			logger.info(
					"DefaultResultFactory: propertiesFile={}, charsetName={}, defaultErrorCode={}, defaultSuccessCode={}, loginExpiredcode={}, parameterErrorCode={}, defaultRollbackOnly={}",
					args);
			return new DefaultAutoBean(beanFactory, "scw.result.DefaultResultFactory", new Class<?>[] { String.class,
					String.class, int.class, int.class, int.class, int.class, String.class, boolean.class }, args);
		}
	}
}
