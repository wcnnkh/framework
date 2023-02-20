package io.basc.framework.factory;

import java.util.function.Supplier;

import io.basc.framework.aop.ConfigurableAop;
import io.basc.framework.convert.TypeDescriptor;

public interface ConfigurableBeanFactory
		extends BeanFactory, SingletonRegistry, BeanDefinitionRegistry, ConfigurableServiceLoaderFactory {
	ConfigurableAop getAop();

	@Override
	ConfigurableBeanResolver getBeanResolver();

	default BeanDefinition registerSupplier(TypeDescriptor typeDescriptor, boolean singleton, Supplier<?> supplier) {
		return register(typeDescriptor.getType().getName(), typeDescriptor, singleton, () -> true, supplier);
	}

	default BeanDefinition registerSupplier(String id, TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<?> supplier) {
		return register(id, typeDescriptor, singleton, () -> true, supplier);
	}

	default BeanDefinition register(TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<Boolean> isInstanceSupplier, Supplier<?> supplier) {
		return register(typeDescriptor.getType().getName(), typeDescriptor, singleton, isInstanceSupplier, supplier);
	}

	BeanDefinition register(String id, TypeDescriptor typeDescriptor, boolean singleton,
			Supplier<Boolean> isInstanceSupplier, Supplier<?> supplier);
}
