package io.basc.framework.factory;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;

public interface BeanResolverExtend {
	default boolean isSingleton(TypeDescriptor type, BeanResolver chain) {
		return chain.isSingleton(type);
	}

	default String getId(TypeDescriptor typeDescriptor, BeanResolver chain) {
		return chain.getId(typeDescriptor);
	}

	default Collection<String> getNames(TypeDescriptor typeDescriptor, BeanResolver chain) {
		return chain.getNames(typeDescriptor);
	}

	default boolean isAopEnable(TypeDescriptor typeDescriptor, BeanResolver chain) {
		return chain.isAopEnable(typeDescriptor);
	}

	default Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		return chain.getDefaultParameter(parameterDescriptor);
	}

	default boolean isAccept(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		return chain.isAccept(parameterDescriptor);
	}

	default Object getParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		return chain.getParameter(parameterDescriptor);
	}

	default boolean isAccept(ParameterDescriptors parameterDescriptors, BeanResolver chain) {
		return chain.isAccept(parameterDescriptors);
	}

	default Object[] getParameters(ParameterDescriptors parameterDescriptors, BeanResolver chain) {
		return chain.getParameters(parameterDescriptors);
	}

	default Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor,
			BeanResolver chain) {
		return chain.resolveDependenceProcessors(typeDescriptor);
	}

	default Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		return chain.resolveInitProcessors(typeDescriptor);
	}

	default Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		return chain.resolveDestroyProcessors(typeDescriptor);
	}
}
