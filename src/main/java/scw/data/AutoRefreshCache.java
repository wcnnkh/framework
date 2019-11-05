package scw.data;

import scw.core.Callable;

public interface AutoRefreshCache extends Cache{
	<T> T get(String key, Callable<? extends T> loader);
}
