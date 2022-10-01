package io.basc.framework.factory.support;

import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ServiceLoaderFactory {

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}

}
