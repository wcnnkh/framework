package scw.beans.builder;

import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class AutoBeanDefinition extends ConstructorBeanDefinition {

	public AutoBeanDefinition(LoaderContext context) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass());
	}

	public AutoBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
	}
}
