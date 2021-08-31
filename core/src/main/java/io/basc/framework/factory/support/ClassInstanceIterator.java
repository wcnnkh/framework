package io.basc.framework.factory.support;

import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.util.AbstractIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ClassInstanceIterator<E> extends AbstractIterator<E>{
	private final Iterator<Class<?>> iterator;
	private final NoArgsInstanceFactory instanceFactory;
	private Class<?> service;
	
	public ClassInstanceIterator(NoArgsInstanceFactory instanceFactory, Iterator<Class<?>> iterator){
		this.instanceFactory = instanceFactory;
		this.iterator = iterator;
	}
	
	public boolean hasNext() {
		while(service == null){
			if(iterator == null || !iterator.hasNext()){
				return false;
			}
			
			service = iterator.next();
			if(service != null && instanceFactory.isInstance(service)){
				return true;
			}else{
				service = null;
			}
		}
		return true;
	}

	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		@SuppressWarnings("unchecked")
		E instance = (E) instanceFactory.getInstance(service);
		service = null;
		return instance;
	}

}