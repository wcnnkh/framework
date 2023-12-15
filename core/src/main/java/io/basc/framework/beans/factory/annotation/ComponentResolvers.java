package io.basc.framework.beans.factory.annotation;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

public class ComponentResolvers extends ConfigurableServices<ComponentResolver> implements ComponentResolver {
	public ComponentResolvers() {
		super(ComponentResolver.class);
		configure(SPI.global());
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(beanDefinition.getReturnTypeDescriptor())) {
				return resolver.getAliasNames(beanDefinition);
			}
		}
		return Elements.empty();
	}

	@Override
	public boolean isComponent(TypeDescriptor typeDescriptor) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(typeDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BeanDefinition createComponent(Class<?> componentClass) {
		TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(componentClass);
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isComponent(typeDescriptor)) {
				return resolver.createComponent(componentClass);
			}
		}
		throw new NoSuchElementException(componentClass.getName());
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isConfiguration(component)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BeanDefinition createBeanDefinition(BeanDefinition component, Method method) {
		for (ComponentResolver resolver : getServices()) {
			if (resolver.isConfiguration(component)) {
				return createBeanDefinition(component, method);
			}
		}
		throw new NoSuchElementException(method.toString());
	}

}
