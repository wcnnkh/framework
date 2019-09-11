package scw.security.session;

import scw.data.cache.MemcachedTemporaryCache;
import scw.data.cache.RedisTemporaryCache;
import scw.data.cache.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private static final String PREFIX = "sid:";
	private String prefix;
	private TemporaryCache temporaryCache;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, String prefix, TemporaryCache temporaryCache) {
		super(defaultMaxInactiveInterval);
		this.prefix = prefix;
		this.temporaryCache = temporaryCache;
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, String prefix, Memcached memcached) {
		this(defaultMaxInactiveInterval, prefix, new MemcachedTemporaryCache(memcached));
	}

	public DefaultSessionFactory(int defaultMaxInactiveInterval, String prefix, Redis redis) {
		this(defaultMaxInactiveInterval, prefix, new RedisTemporaryCache(redis));
	}

	protected String getKey(String sessionId) {
		return prefix == null ? (PREFIX + sessionId) : (PREFIX + prefix + sessionId);
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return (SessionData) temporaryCache.get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		temporaryCache.set(sessionData.getSessionId(), sessionData.getMaxInactiveInterval(), sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		temporaryCache.delete(sessionId);
	}

}
