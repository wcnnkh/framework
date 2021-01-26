package scw.util;

public final class StaticSupplier<T> implements Supplier<T>{
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
