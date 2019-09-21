package scw.security.session;

import scw.data.cache.MemcachedTemporaryCache;
import scw.data.cache.RedisTemporaryCache;
import scw.data.cache.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private TemporaryCache temporaryCache;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, TemporaryCache temporaryCache) {
		super(defaultMaxInactiveInterval);
		this.temporaryCache = temporaryCache;
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, Memcached memcached) {
		this(defaultMaxInactiveInterval, new MemcachedTemporaryCache(memcached));
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, Redis redis) {
		this(defaultMaxInactiveInterval, new RedisTemporaryCache(redis));
	}

	protected String getKey(String sessionId) {
		return "session-factory:" + sessionId;
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return (SessionData) temporaryCache.get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		temporaryCache.set(getKey(sessionData.getSessionId()), sessionData.getMaxInactiveInterval(), sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		temporaryCache.delete(getKey(sessionId));
	}

}
