package io.basc.framework.context.annotation;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.instance.ServiceLoader;
import io.basc.framework.instance.support.ClassInstanceIterator;

import java.util.Iterator;

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
