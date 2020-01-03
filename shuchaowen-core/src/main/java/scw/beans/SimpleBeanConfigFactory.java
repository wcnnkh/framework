package scw.beans;

import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;

public interface SimpleBeanConfigFactory extends BeanConfigFactory {
	void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory);
}
