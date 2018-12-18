package scw.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class AbstractCacheFactory implements CacheFactory{
	private Map<String, CallableInfo<?>> callableMap = new HashMap<String, CallableInfo<?>>();
	private final int exp;
	private final boolean lock;
	
	public AbstractCacheFactory(int exp, boolean lock){
		this.exp = exp;
		this.lock = lock;
	}
	
	public synchronized <T> void register(String key, Class<T> type, Callable<T> callable) {
		callableMap.put(key, new CallableInfo<T>(type, callable));
	}
	
	@SuppressWarnings("unchecked")
	public <T> CallableInfo<T> getCallable(String key){
		return (CallableInfo<T>) callableMap.get(key);
	}

	public int getExp() {
		return exp;
	}

	public boolean isLock() {
		return lock;
	}
}

class CallableInfo<T>{
	private final Callable<T> callable;
	private final Class<T> type;
	
	public CallableInfo(Class<T> type, Callable<T> callable){
		this.callable = callable;
		this.type = type;
	}

	public Callable<T> getCallable() {
		return callable;
	}

	public Class<T> getType() {
		return type;
	}
}
