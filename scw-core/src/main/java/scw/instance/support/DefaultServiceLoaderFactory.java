package scw.instance.support;

import scw.core.Constants;
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
	
	protected boolean useSpi(Class<?> serviceClass){
		return serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> service) {
		if(useSpi(service)){
			return new ServiceLoaders<S>(new ConfigServiceLoader<S>(service,
					getConfigFactory(), getInstanceFactory()),
					super.getServiceLoader(service));
		}else{
			return super.getServiceLoader(service);
		}
	}
}
