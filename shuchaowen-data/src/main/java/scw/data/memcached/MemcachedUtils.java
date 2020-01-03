package scw.data.memcached;

import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class MemcachedUtils {
	private MemcachedUtils(){};
	
	public static boolean startingFlushAll(){
		return StringUtils.parseBoolean(SystemPropertyUtils.getProperty("memcached.starting.flush"));
	}
}
