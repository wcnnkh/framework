package scw.instance.support;

import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.instance.NoArgsInstanceFactory;
import scw.util.AbstractIterator;

public class ClassInstanceIterator<E> extends AbstractIterator<E>{
	private final Iterator<Class<E>> iterator;
	private final NoArgsInstanceFactory instanceFactory;
	private Class<E> service;
	
	public ClassInstanceIterator(NoArgsInstanceFactory instanceFactory, Iterator<Class<E>> iterator){
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

		E instance = instanceFactory.getInstance(service);
		service = null;
		return instance;
	}

}
