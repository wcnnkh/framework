package scw.beans.auto;

import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DB;
import scw.logger.LazyLogger;
import scw.logger.Logger;
import scw.result.ResultFactory;

public final class DefaultAutoBeanService implements AutoBeanService {
	private static Logger logger = new LazyLogger(DefaultAutoBeanService.class);

	private AutoBean defaultService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		AutoBean autoBean = null;
		if (clazz == Memcached.class) {
			autoBean = createMemcached(beanFactory, propertyFactory);
		} else if (clazz == ResultFactory.class) {
			autoBean = createResultFactory(beanFactory, propertyFactory);
		} else if (clazz == DB.class) {
			autoBean = createDB(beanFactory, propertyFactory);
		} else if (clazz == Redis.class) {
			autoBean = createRedis(beanFactory, propertyFactory);
		}

		if (autoBean != null) {
			return autoBean;
		}

		// 未注解service时接口默认实现
		if (clazz.isInterface()) {
			String name = clazz.getName() + "Impl";
			if (ClassUtils.isExist(name) && beanFactory.contains(name)) {
				logger.info("{} reference {}", clazz.getName(), name);
				return new ReferenceAutoBean(beanFactory, name);
			} else {
				int index = clazz.getName().lastIndexOf(".");
				name = index == -1 ? (clazz.getName() + "Impl") : (clazz
						.getName().substring(0, index)
						+ ".impl."
						+ clazz.getSimpleName() + "Impl");
				if (ClassUtils.isExist(name) && beanFactory.contains(name)) {
					logger.info("{} reference {}", clazz.getName(), name);
					return new ReferenceAutoBean(beanFactory, name);
				}
			}
		}

		if (!ReflectUtils.isInstance(clazz, false)) {
			return serviceChain.service(clazz, beanFactory, propertyFactory);
		}

		return new SimpleAutoBean(beanFactory, clazz, propertyFactory);
	}

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		AutoImpl autoConfig = clazz.getAnnotation(AutoImpl.class);
		if (autoConfig == null) {
			return defaultService(clazz, beanFactory, propertyFactory,
					serviceChain);
		}

		Collection<Class<?>> implList = getAutoImplClass(autoConfig, clazz, propertyFactory);
		if (CollectionUtils.isEmpty(implList)) {
			return defaultService(clazz, beanFactory, propertyFactory,
					serviceChain);
		}

		AutoBean autoBean = null;
		for (Class<?> clz : implList) {
			autoBean = AutoBeanUtils.autoBeanService(clz, autoConfig,
					beanFactory, propertyFactory);
			if (autoBean != null) {
				break;
			}
		}
		return autoBean;
	}

	private static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig,
			Class<?> type, PropertyFactory propertyFactory) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.implClassName()) {
			if(StringUtils.isEmpty(name)){
				continue;
			}
			
			name = FormatUtils.format(name, propertyFactory, true);
			Class<?> clz = null;
			try {
				clz = Class.forName(name);
			} catch (ClassNotFoundException e) {
			}

			if (clz == null) {
				continue;
			}

			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			}
		}

		for (Class<?> clz : autoConfig.impl()) {
			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			}
		}

		return list;
	}

	private AutoBean createMemcached(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		if (ClassUtils.isExist("scw.data.memcached.x.XMemcached")) {
			String host = propertyFactory.getProperty("memcached.hosts");
			if (!StringUtils.isEmpty(host)) {
				logger.info("using default memcached config:{}", host);
				return new DefaultAutoBean(beanFactory,
						"scw.data.memcached.x.XMemcached",
						new Class<?>[] { String.class }, new Object[] { host });
			}
		}
		return null;
	}

	private AutoBean createResultFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		String resultPropertiesFile = propertyFactory
				.getProperty("result.config");
		String charsetName = propertyFactory.getProperty("result.charset");
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Constants.DEFAULT_CHARSET_NAME;
		}

		int defaultErrorCode = StringUtils.parseInt(
				propertyFactory.getProperty("result.error.code"), 1);
		int defaultSuccessCode = StringUtils.parseInt(
				propertyFactory.getProperty("result.success.code"), 0);
		int loginExpiredCode = StringUtils.parseInt(
				propertyFactory.getProperty("result.expired.code"), -1);
		int parameterErrorCode = StringUtils.parseInt(
				propertyFactory.getProperty("result.parameter.error.code"), 1);
		String contentType = propertyFactory.getProperty("result.contentType");
		if (StringUtils.isEmpty(contentType)) {
			contentType = "application/json";
		}

		boolean defaultRollbackOnly = StringUtils.parseBoolean(
				propertyFactory.getProperty("result.rollbackOnly"), true);
		if (ClassUtils.isExist("javax.servlet.Servlet")) {
			Object[] args = new Object[] { resultPropertiesFile, charsetName,
					defaultErrorCode, defaultSuccessCode, loginExpiredCode,
					parameterErrorCode, contentType, defaultRollbackOnly };
			logger.info(
					"ServletResultFactory: propertiesFile={}, charsetName={}, defaultErrorCode={}, defaultSuccessCode={}, loginExpiredcode={}, parameterErrorCode={}, contentType={}, defaultRollbackOnly={}",
					args);
			return new DefaultAutoBean(beanFactory,
					"scw.result.servlet.ServletResultFactory",
					new Class<?>[] { String.class, String.class, int.class,
							int.class, int.class, int.class, String.class,
							boolean.class }, args);
		} else {
			Object[] args = new Object[] { resultPropertiesFile, charsetName,
					defaultErrorCode, defaultSuccessCode, loginExpiredCode,
					parameterErrorCode, defaultRollbackOnly };
			logger.info(
					"DefaultResultFactory: propertiesFile={}, charsetName={}, defaultErrorCode={}, defaultSuccessCode={}, loginExpiredcode={}, parameterErrorCode={}, defaultRollbackOnly={}",
					args);
			return new DefaultAutoBean(beanFactory,
					"scw.result.DefaultResultFactory",
					new Class<?>[] { String.class, String.class, int.class,
							int.class, int.class, int.class, String.class,
							boolean.class }, args);
		}
	}

	private AutoBean createDB(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		String config = "classpath:/db.properties";
		if (ResourceUtils.isExist(config)) {
			Object[] args;
			Class<?>[] types;
			if (beanFactory.contains(Redis.class.getName())) {
				types = new Class<?>[] { Redis.class, String.class };
				args = new Object[] { beanFactory.getInstance(Redis.class),
						config };
			} else if (beanFactory.contains(Memcached.class.getName())) {
				types = new Class<?>[] { Memcached.class, String.class };
				args = new Object[] { beanFactory.getInstance(Memcached.class),
						config };
			} else {
				types = new Class<?>[] { String.class };
				args = new Object[] { config };
			}

			if (ClassUtils.isExist("scw.db.DruidDB")) {
				logger.info("init scw.db.DruidDB");
				return new DefaultAutoBean(beanFactory, "scw.db.DruidDB",
						types, args);
			} else if (ClassUtils.isExist("scw.db.HikariCPDB")) {
				logger.info("init scw.db.HikariCPDB");
				return new DefaultAutoBean(beanFactory, "scw.db.HikariCPDB",
						types, args);
			}
		}

		return null;
	}

	private AutoBean createRedis(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		String config = "classpath:/redis.properties";
		if (ResourceUtils.isExist(config)) {
			Object[] args = new Object[] { config };
			Class<?>[] types = new Class<?>[] { String.class };
			if (ClassUtils.isExist("scw.data.redis.jedis.RedisByJedisPool")) {
				logger.info("init scw.data.redis.jedis.RedisByJedisPool");
				return new DefaultAutoBean(beanFactory,
						"scw.data.redis.jedis.RedisByJedisPool", types, args);
			}
		}
		return null;
	}
}
