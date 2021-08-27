package io.basc.framework.util;

public class CacheableSupplier<T> implements Supplier<T>{
	private final java.util.function.Supplier<T> supplier;
	private volatile java.util.function.Supplier<T> cache;
	
	public CacheableSupplier(java.util.function.Supplier<T> supplier){
		this.supplier = supplier;
	}
	
	public T get() {
		if(cache == null){
			synchronized (this) {
				if(cache == null){
					setCache(forceGet());
				}
			}
		}
		return cache.get();
	}
	
	public T forceGet(){
		return supplier.get();
	}

	public void setCache(T cache){
		this.cache = new StaticSupplier<T>(cache);
	}

	/**
	 * 刷新
	 */
	public void refresh(){
		synchronized (this) {
			setCache(forceGet());
		}
	}
}
