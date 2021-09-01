package io.basc.framework.aop.support;

import io.basc.framework.aop.ProxyFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public final class UnmodifiableProxyFactories extends AbstractConfigurableProxyFactory{
	private final Iterable<ProxyFactory> iterable;
	
	public UnmodifiableProxyFactories(Iterable<ProxyFactory> iterable){
		this.iterable = iterable;
	}
	
	public UnmodifiableProxyFactories(ProxyFactory ...proxyFactories){
		this(Arrays.asList(proxyFactories));
	}
	
	@Override
	public Iterator<ProxyFactory> iterator() {
		if(iterable == null){
			return Collections.emptyIterator();
		}
		return iterable.iterator();
	}

}
