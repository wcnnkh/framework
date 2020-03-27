package scw.beans.auto;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.instance.AutoInstanceConfig;
import scw.core.instance.InstanceConfig;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public class SimpleAutoBean extends AbstractSimpleAutoBean {
	private static Logger logger = LoggerUtils.getLogger(SimpleAutoBean.class);
	private InstanceConfig instanceConfig;

	public SimpleAutoBean(BeanFactory beanFactory, Class<?> type, PropertyFactory propertyFactory) {
		super(beanFactory, type);
		this.instanceConfig = new AutoInstanceConfig(beanFactory, propertyFactory, type);
		if (instanceConfig.getConstructor() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(instanceConfig.getConstructor());
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

	@Override
	protected Collection<String> getFilterNames() {
		return null;
	}
}
