package io.basc.framework.factory.support;

import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.util.AbstractIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class InstanceIterator<E> extends AbstractIterator<E>{
	private final NoArgsInstanceFactory instanceFactory;
	private final Iterator<String> iterator;
	private String name;
	
	public InstanceIterator(NoArgsInstanceFactory instanceFactory, Iterator<String> iterator){
		this.instanceFactory = instanceFactory;
		this.iterator = iterator;
	}
	
	public boolean hasNext() {
		while(name == null){
			if(iterator == null || !iterator.hasNext()){
				return false;
			}
			
			name =iterator.next();
			if(name != null && instanceFactory.isInstance(name)){
				return true;
			}else{
				name = null;
			}
		}
		return true;
	}

	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		E instance = instanceFactory.getInstance(name);
		name = null;
		return instance;
	}

}
