package io.basc.framework.factory.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.FactoryResolver;

public class FactoryResolverConfiguration implements FactoryResolver {
	private boolean singleton;

	@Override
	public boolean isSingleton(TypeDescriptor type) {
		return isSingleton();
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
}
