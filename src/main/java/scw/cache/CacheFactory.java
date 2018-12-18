package scw.cache;

import java.util.concurrent.Callable;

public interface CacheFactory{
	<T> T get(String key);
	
	void delete(String key);

	<T> void register(String key, Class<T> rtnType, Callable<T> callable);
}
