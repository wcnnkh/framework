package io.basc.framework.factory.support;

import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.ServiceLoader;

/**
 * @see SpiServiceLoader
 * @author wcnnkh
 *
 */
public class SimpleServiceLoaderFactory extends SimpleInstanceFactory implements ServiceLoaderFactory {

	public static final ServiceLoaderFactory INSTANCE = new SimpleServiceLoaderFactory();

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}
}
