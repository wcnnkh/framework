package scw.beans;

import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.exception.NotSupportException;

public abstract class AbstractInterfaceBeanDefinition extends AbstractBeanDefinition {

	public AbstractInterfaceBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
	}

	public boolean isInstance() {
		return true;
	}

	@Override
	public boolean isProxy() {
		return true;
	}

	public <T> T create(Object... params) {
		throw new NotSupportException(getType().getName());
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		throw new NotSupportException(getType().getName());
	}
}
