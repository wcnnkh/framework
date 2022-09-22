package io.basc.framework.factory;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;

public interface BeanResolver extends DefaultParameterFactory, ParameterFactory {
	boolean isSingleton(TypeDescriptor typeDescriptor);

	String getId(TypeDescriptor typeDescriptor);

	Collection<String> getNames(TypeDescriptor typeDescriptor);

	boolean isAopEnable(TypeDescriptor typeDescriptor);
	
	Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor);
}