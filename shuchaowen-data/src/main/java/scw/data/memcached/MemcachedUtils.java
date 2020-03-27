package scw.data.memcached;

import scw.core.GlobalPropertyFactory;

public final class MemcachedUtils {
	private MemcachedUtils(){};
	
	public static boolean startingFlushAll(){
		return GlobalPropertyFactory.getInstance().getBooleanValue("memcached.starting.flush");
	}
}
