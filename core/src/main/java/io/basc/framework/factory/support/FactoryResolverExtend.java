package io.basc.framework.factory.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.FactoryResolver;

public interface FactoryResolverExtend {
	default boolean isSingleton(TypeDescriptor type, FactoryResolver chain) {
		return chain.isSingleton(type);
	}
}
