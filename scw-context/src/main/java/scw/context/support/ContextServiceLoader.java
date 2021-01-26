package scw.context.support;

import java.util.Iterator;

import scw.context.ClassesLoader;
import scw.context.annotation.ProviderServiceLoader;
import scw.env.Environment;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.util.MultiIterator;

public class ContextServiceLoader<S> implements ServiceLoader<S>{
	private final ServiceLoader<S> defaultServiceLoader;
	private final ProviderServiceLoader<S> providerServiceLoader;
	
	public ContextServiceLoader(Class<S> serviceClass, NoArgsInstanceFactory instanceFactory, Environment environment, ClassesLoader<?> classesLoader){
		this.defaultServiceLoader = InstanceUtils.getServiceLoader(serviceClass, instanceFactory, environment);
		this.providerServiceLoader = new ProviderServiceLoader<S>(classesLoader, instanceFactory, serviceClass);
	}
	
	public void reload() {
		defaultServiceLoader.reload();
		providerServiceLoader.reload();
	}

	@SuppressWarnings("unchecked")
	public Iterator<S> iterator() {
		return new MultiIterator<S>(defaultServiceLoader.iterator(), providerServiceLoader.iterator());
	}

}
