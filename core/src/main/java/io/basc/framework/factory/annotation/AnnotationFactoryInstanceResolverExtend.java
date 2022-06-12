package io.basc.framework.factory.annotation;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.factory.FactoryResolver;
import io.basc.framework.factory.support.FactoryResolverExtend;

public class AnnotationFactoryInstanceResolverExtend implements FactoryResolverExtend {

	@Override
	public boolean isSingleton(TypeDescriptor type, FactoryResolver chain) {
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
}
