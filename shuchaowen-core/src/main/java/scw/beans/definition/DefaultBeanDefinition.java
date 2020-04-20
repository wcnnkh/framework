package scw.beans.definition;

import scw.beans.BeanFactory;
import scw.beans.definition.builder.BeanBuilder;
import scw.util.value.property.PropertyFactory;

public class DefaultBeanDefinition extends AbstractBeanDefinition {
	private final BeanBuilder beanBuilder;

	public DefaultBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			BeanBuilder beanBuilder) {
		super(beanFactory, propertyFactory, targetClass);
		this.beanBuilder = beanBuilder;
	}

	@Override
	protected BeanBuilder getBeanBuiler() {
		return beanBuilder;
	}

}
