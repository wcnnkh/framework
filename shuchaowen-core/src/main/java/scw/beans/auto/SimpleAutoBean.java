package scw.beans.auto;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.instance.definition.AutoConstructorDefinition;
import scw.core.instance.definition.ConstructorDefinition;
import scw.core.parameter.ParameterUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public class SimpleAutoBean extends AbstractSimpleAutoBean {
	private static Logger logger = LoggerUtils.getLogger(SimpleAutoBean.class);
	private ConstructorDefinition constructorDefinition;

	public SimpleAutoBean(BeanFactory beanFactory, Class<?> type,
			PropertyFactory propertyFactory) {
		super(beanFactory, type);
		this.constructorDefinition = new AutoConstructorDefinition(beanFactory,
				propertyFactory, type,
				ParameterUtils.getParameterDescriptorFactory());
		if (constructorDefinition.getConstructor() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(constructorDefinition.getConstructor());
			}
		}
	}

	public boolean isInstance() {
		return constructorDefinition.getConstructor() != null;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return constructorDefinition.getConstructor() == null ? null
				: constructorDefinition.getConstructor().getParameterTypes();
	}

	@Override
	protected Object[] getParameters() {
		return constructorDefinition.getArgs();
	}

	@Override
	protected Collection<String> getFilterNames() {
		return null;
	}
}
