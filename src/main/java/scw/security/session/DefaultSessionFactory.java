package scw.security.session;

import scw.data.cache.CacheService;
import scw.data.cache.MemcachedCacheService;
import scw.data.cache.RedisCacheService;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private CacheService cacheService;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, CacheService cacheService) {
		super(defaultMaxInactiveInterval);
		this.cacheService = cacheService;
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, Memcached memcached) {
		this(defaultMaxInactiveInterval, new MemcachedCacheService(memcached));
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, Redis redis) {
		this(defaultMaxInactiveInterval, new RedisCacheService(redis));
	}

	protected String getKey(String sessionId) {
		return "session-factory:" + sessionId;
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return (SessionData) cacheService.get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		cacheService.set(getKey(sessionData.getSessionId()), sessionData.getMaxInactiveInterval(), sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		cacheService.delete(getKey(sessionId));
	}

}
