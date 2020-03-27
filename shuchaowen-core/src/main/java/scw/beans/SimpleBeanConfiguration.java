package scw.beans;

import scw.beans.property.ValueWiredManager;
import scw.util.value.property.PropertyFactory;

public interface SimpleBeanConfiguration extends BeanConfiguration {
	void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory);
}
