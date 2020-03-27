package scw.beans;

import scw.beans.property.ValueWiredManager;
import scw.lang.NotSupportException;
import scw.util.value.property.PropertyFactory;

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

	public <T> T create(Object... params) throws Exception{
		throw new NotSupportException(getType().getName());
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) throws Exception{
		throw new NotSupportException(getType().getName());
	}

	public String[] getNames() {
		return null;
	}
}
