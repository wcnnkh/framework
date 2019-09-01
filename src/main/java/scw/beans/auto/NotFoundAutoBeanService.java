package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoConfig;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.logger.LazyLogger;
import scw.logger.Logger;
import scw.result.ResultFactory;

public final class NotFoundAutoBeanService implements AutoBeanService {
	private static Logger logger = new LazyLogger(NotFoundAutoBeanService.class);

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		AutoConfig autoConfig = clazz.getAnnotation(AutoConfig.class);
		if (autoConfig == null || autoConfig.service() == Object.class
				|| autoConfig.service() == clazz) {
			AutoBean autoBean = null;
			if (clazz == Memcached.class) {
				autoBean = createMemcached(beanFactory, propertyFactory);
			} else if (clazz == ResultFactory.class) {
				autoBean = createResultFactory(beanFactory, propertyFactory);
			}

			if (autoBean != null) {
				return autoBean;
			}
			
			if (!ReflectUtils.isInstance(clazz, false)) {
				return serviceChain
						.service(clazz, beanFactory, propertyFactory);
			}

			return new DefaultAutoBean(beanFactory, clazz);
		}

		return AutoBeanUtils.autoBeanService(autoConfig.service(), autoConfig,
				beanFactory, propertyFactory);
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
}
