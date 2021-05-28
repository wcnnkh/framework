package scw.instance.support;

import scw.instance.AbstractServiceLoaderFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.value.ValueFactory;

public class DefaultServiceLoaderFactory extends AbstractServiceLoaderFactory {
	private final NoArgsInstanceFactory instanceFactory;
	private final ValueFactory<String> configFactory;

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
