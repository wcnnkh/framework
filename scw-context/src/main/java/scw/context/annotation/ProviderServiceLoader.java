package scw.context.annotation;

import java.util.Iterator;

import scw.context.ClassesLoader;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.support.ClassInstanceIterator;

public class ProviderServiceLoader<S> implements ServiceLoader<S>{
	private final ClassesLoader providers;
	private final NoArgsInstanceFactory instanceFactory;
	
	public ProviderServiceLoader(ClassesLoader classesLoader, NoArgsInstanceFactory instanceFactory, Class<S> serviceClass){
		this(new ProviderClassesLoader(classesLoader, serviceClass), instanceFactory);
	}
	
	public ProviderServiceLoader(ClassesLoader providers, NoArgsInstanceFactory instanceFactory){
		this.providers = providers;
		this.instanceFactory = instanceFactory;
	}
	
	public void reload() {
		this.providers.reload();
	}
	
	public Iterator<S> iterator() {
		return new ClassInstanceIterator<S>(instanceFactory, providers.iterator());
	}
}
