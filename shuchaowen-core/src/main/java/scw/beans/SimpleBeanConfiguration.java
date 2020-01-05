package scw.beans;

import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;

public interface SimpleBeanConfiguration extends BeanConfiguration {
	void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory);
}
