package io.basc.framework.autoconfigure.beans.factory;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.support.PropertyAutowiredBeanPostProcessor;
import io.basc.framework.beans.factory.ioc.BeanRegistrationManager;
import io.basc.framework.env.PropertyFactory;
import io.basc.framework.mapper.Setter;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.MappingRegistry;
import io.basc.framework.util.Elements;

/**
 * value注解的实现
 * 
 * @author wcnnkh
 *
 */
class AutowriedValueBeanPostProcessor extends PropertyAutowiredBeanPostProcessor {
	private final BeanFactory beanFactory;

	public AutowriedValueBeanPostProcessor(MappingRegistry mappingFactory, PropertyFactory propertyFactory,
			BeanRegistrationManager beanRegistrationManager, BeanFactory beanFactory) {
		super(mappingFactory, propertyFactory, beanRegistrationManager);
		this.beanFactory = beanFactory;
	}

	@Override
	protected boolean isSingleton(String beanName) {
		return beanFactory.isSingleton(beanName);
	}

	@Override
	protected Elements<String> getPropertyNames(FieldDescriptor field) {
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
	protected boolean canAutwired(Object bean, String beanName, FieldDescriptor field) {
		return field.getSetters().anyMatch((e) -> e.getTypeDescriptor().hasAnnotation(Value.class));
	}

}
