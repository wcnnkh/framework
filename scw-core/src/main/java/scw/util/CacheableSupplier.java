package scw.util;

public class CacheableSupplier<T> implements Supplier<T>{
	private final Supplier<T> supplier;
	private volatile Supplier<T> cache;
	
	public CacheableSupplier(Supplier<T> supplier){
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
