package scw.beans;

import scw.beans.definition.BeanDefinition;
import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);
}
