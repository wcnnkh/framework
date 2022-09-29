package io.basc.framework.factory.annotation;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.support.InstanceParameterFactory;
import io.basc.framework.lang.Nullable;

public class AnnotationFactoryInstanceResolverExtend extends InstanceParameterFactory implements BeanResolverExtend {

	public AnnotationFactoryInstanceResolverExtend(InstanceFactory instanceFactory) {
		super(instanceFactory);
	}

	@Override
	public boolean isSingleton(TypeDescriptor type, BeanResolver chain) {
		Singleton singleton = AnnotatedElementUtils.getMergedAnnotation(type, Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		Boolean b = isSingleton(type.getType());
		if (b != null) {
			return b;
		}
		return chain.isSingleton(type);
	}

	private Boolean isSingleton(Class<?> type) {
		if (type == null) {
			return null;
		}

		Singleton singleton = AnnotatedElementUtils.getMergedAnnotation(type, Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		Boolean b = isSingleton(type.getSuperclass());
		if (b != null) {
			return b;
		}

		for (Class<?> interfaceClass : type.getInterfaces()) {
			b = isSingleton(interfaceClass);
			if (b != null) {
				return b;
			}
		}
		return null;
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		Nullable nullable = parameterDescriptor.getAnnotation(Nullable.class);
		if (nullable != null) {
			return nullable.value();
		}
		return super.isNullable(parameterDescriptor, chain);
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return super.getDefaultParameter(parameterDescriptor, chain);
	}
}
