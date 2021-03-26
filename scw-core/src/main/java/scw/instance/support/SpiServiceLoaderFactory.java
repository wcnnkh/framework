package scw.instance.support;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;

public class SpiServiceLoaderFactory extends AbstractServiceLoaderFactory{
	
	public SpiServiceLoaderFactory(NoArgsInstanceFactory instanceFactory){
		super(instanceFactory);
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, getInstanceFactory());
	}
}
