package io.basc.framework.beans.factory.annotation.procesor;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.annotation.ComponentResolver;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

public class AnnotationComponentResolver implements ComponentResolver {

	@Override
	public boolean isComponent(TypeDescriptor typeDescriptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BeanDefinition createComponent(Class<?> componentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BeanDefinition createBeanDefinition(BeanDefinition component, Method method) {
		// TODO Auto-generated method stub
		return null;
	}

}
