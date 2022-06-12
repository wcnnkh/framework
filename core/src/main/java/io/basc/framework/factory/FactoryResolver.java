package io.basc.framework.factory;

import io.basc.framework.convert.TypeDescriptor;

public interface FactoryResolver {
	boolean isSingleton(TypeDescriptor type);
}