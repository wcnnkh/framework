package io.basc.framework.factory.supplier;

import io.basc.framework.util.Supplier;

public class SimpleInstanceSupplier<T> implements Supplier<T>{
	private T instance;
	
	public SimpleInstanceSupplier(T instance){
		this.instance = instance;
	}
	
	public T get() {
		return instance;
	}

}
