package io.basc.framework.context.ioc.annotation;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.support.BeanRegistrationManager;
import io.basc.framework.beans.factory.config.support.PropertyAutowiredBeanPostProcessor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.PropertyFactory;

/**
 * value注解的实现
 * 
 * @author wcnnkh
 *
 */
class AutowriedValueBeanPostProcessor extends PropertyAutowiredBeanPostProcessor {
	private final BeanFactory beanFactory;

	public AutowriedValueBeanPostProcessor(MappingFactory mappingFactory, PropertyFactory propertyFactory,
			BeanRegistrationManager beanRegistrationManager, BeanFactory beanFactory) {
		super(mappingFactory, propertyFactory, beanRegistrationManager);
		this.beanFactory = beanFactory;
	}

	@Override
	protected boolean isSingleton(String beanName) {
		return beanFactory.isSingleton(beanName);
	}

	@Override
	protected Elements<String> getPropertyNames(Field field) {
		for (Setter setter : field.getSetters()) {
			Value value = setter.getTypeDescriptor().getAnnotation(Value.class);
			if (value == null) {
				continue;
			}

			return Elements.singleton(setter.getName());
		}
		return Elements.empty();
	}

	@Override
	protected boolean canAutwired(Object bean, String beanName, Field field) {
		return field.getSetters().anyMatch((e) -> e.getTypeDescriptor().hasAnnotation(Value.class));
	}

}
