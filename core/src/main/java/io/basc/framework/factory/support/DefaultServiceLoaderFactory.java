package io.basc.framework.factory.support;

import io.basc.framework.factory.AbstractServiceLoaderFactory;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.value.ValueFactory;
import io.basc.framework.value.support.SystemPropertyFactory;

public class DefaultServiceLoaderFactory extends AbstractServiceLoaderFactory {

	public static final ServiceLoaderFactory INSTANCE = new DefaultServiceLoaderFactory();

	private final NoArgsInstanceFactory instanceFactory;
	private final ValueFactory<String> configFactory;

	public DefaultServiceLoaderFactory() {
		this(SystemPropertyFactory.INSTANCE);
	}

	/**
	 * @param configFactory
	 */
	public DefaultServiceLoaderFactory(ValueFactory<String> configFactory) {
		this(new SimpleNoArgsInstanceFactory(), configFactory);
	}

	/**
	 * @param instanceFactory
	 * @param configFactory
	 */
	public DefaultServiceLoaderFactory(NoArgsInstanceFactory instanceFactory, ValueFactory<String> configFactory) {
		this.instanceFactory = instanceFactory;
		this.configFactory = configFactory;
		setForceSpi(true);
	}

	@Override
	protected ValueFactory<String> getConfigFactory() {
		return configFactory;
	}

	@Override
	protected NoArgsInstanceFactory getTargetInstanceFactory() {
		return instanceFactory;
	}
}
