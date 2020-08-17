package scw.beans.builder;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public abstract class ConstructorBeanDefinition extends AbstractBeanDefinition {
	
	public ConstructorBeanDefinition(LoaderContext context) {
		super(context);
	}
	
	public ConstructorBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
	}
}
