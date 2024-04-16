package io.basc.framework.autoconfigure.beans.factory;

import java.lang.reflect.Method;
import java.util.Arrays;

import io.basc.framework.beans.factory.component.ComponentResolver;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.ioc.IocResolver;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.util.element.Elements;

public class AnnotationComponentResolver implements ComponentResolver, IocResolver {

	@Override
	public boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComponent(AnnotatedTypeMetadata annotatedTypeMetadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Elements<String> getAliasNames(BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BeanDefinition createComponent(AnnotationMetadata componentAnnotationMetadata, ClassLoader classLoader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfiguration(BeanDefinition component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BeanDefinition createComponent(BeanDefinition component, MethodMetadata methodMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInitMethod(Method method) {
		Bean bean = method.getAnnotation(Bean.class);
		if (bean != null) {
			if (Arrays.asList(bean.initMethod()).contains(method.getName())) {
				return true;
			}
		}
		return method.isAnnotationPresent(InitMethod.class);
	}

	@Override
	public boolean isDestroyMethod(Method method) {
		Bean bean = method.getAnnotation(Bean.class);
		if (bean != null) {
			if (Arrays.asList(bean.destroyMethod()).contains(method.getName())) {
				return true;
			}
		}
		return method.isAnnotationPresent(Destroy.class);
	}

}
