package io.basc.framework.instance.support;

import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.instance.ServiceLoader;

import java.util.Arrays;
import java.util.Iterator;

public final class StaticServiceLoader<S> implements ServiceLoader<S>{
	private final Iterable<String> names;
	private final NoArgsInstanceFactory instanceFactory;
	
	public StaticServiceLoader(NoArgsInstanceFactory instanceFactory, Iterable<String> names){
		this.instanceFactory = instanceFactory;
		this.names = names;
	}
	
	public StaticServiceLoader(NoArgsInstanceFactory instanceFactory, String ...names){
		this(instanceFactory, Arrays.asList(names));
	}
	
	public void reload() {
	}

	public Iterator<S> iterator() {
		return new InstanceIterator<S>(instanceFactory, names.iterator());
	}

}
