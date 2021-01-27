package scw.instance.support;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;
import scw.lang.Nullable;
import scw.util.ClassLoaderProvider;

public class SpiServiceLoaderFactory implements ServiceLoaderFactory {
	private NoArgsInstanceFactory instanceFactory;
	private ClassLoaderProvider classLoaderProvider;

	public SpiServiceLoaderFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		this.classLoaderProvider = instanceFactory;
	}
	
	public SpiServiceLoaderFactory(ClassLoaderProvider classLoaderProvider){
		this.classLoaderProvider = classLoaderProvider;
	}
	
	@Nullable
	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public ClassLoader getClassLoader() {
		return classLoaderProvider == null? classLoaderProvider.getClassLoader():instanceFactory.getClassLoader();
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> service) {
		if(classLoaderProvider != null){
			return new SpiServiceLoader<S>(service, getClassLoader());
		}
		return new SpiServiceLoader<S>(service, getInstanceFactory());
	}
}
