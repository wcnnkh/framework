package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.instance.AutoInstanceConfig;
import scw.core.instance.InstanceConfig;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class SimpleAutoBean extends AbstractAutoBean {
	private static Logger logger = LoggerUtils.getLogger(SimpleAutoBean.class);
	private InstanceConfig instanceConfig;

	public SimpleAutoBean(BeanFactory beanFactory, Class<?> type, PropertyFactory propertyFactory) {
		super(beanFactory, type);
		this.instanceConfig = new AutoInstanceConfig(beanFactory, propertyFactory, type);
		if (instanceConfig.getConstructor() != null) {
			// 默认的构造方法不显示日志
			if (instanceConfig.getConstructor().getParameterTypes().length != 0) {
				logger.info(instanceConfig.getConstructor());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(instanceConfig.getConstructor());
				}
			}
		}
	}

	public boolean isInstance() {
		return instanceConfig.getConstructor() != null;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return instanceConfig.getConstructor() == null ? null : instanceConfig.getConstructor().getParameterTypes();
	}

	@Override
	protected Object[] getParameters() {
		return instanceConfig.getArgs();
	}

}
