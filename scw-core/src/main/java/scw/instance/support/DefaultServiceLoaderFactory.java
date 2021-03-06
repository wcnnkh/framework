package scw.instance.support;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.value.factory.ValueFactory;

public class DefaultServiceLoaderFactory extends SpiServiceLoaderFactory {
	private final ValueFactory<String> configFactory;

	public DefaultServiceLoaderFactory(ValueFactory<String> configFactory,
			NoArgsInstanceFactory instanceFactory) {
		super(instanceFactory);
		this.configFactory = configFactory;
	}

	public ValueFactory<String> getConfigFactory() {
		return configFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> service) {
		ValueFactory<String> configFactory = getConfigFactory();
		if(configFactory != null){
			return new ServiceLoaders<S>(new ConfigServiceLoader<S>(service, configFactory, getInstanceFactory()),
					super.getServiceLoader(service));
		}else{
			return super.getServiceLoader(service);
		}
	}
}
