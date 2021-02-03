package scw.util;

import java.io.Serializable;

public final class StaticSupplier<T> implements Supplier<T>, Serializable{
	private static final long serialVersionUID = 1L;
	private final T source;
	
	public StaticSupplier(T source){
		this.source = source;
	}
	
	public T get() {
		return source;
	}

	@Override
	public String toString() {
		return String.valueOf(source);
	}
}
