package scw.security.limit;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ MemcachedCountLimitFactory.class, RedisCountLimitFactory.class, MemoryCountLimitFactory.class })
public interface CountLimitFactory {
	CountLimit getCountLimit(CountLimitConfig countLimitConfig);
}
