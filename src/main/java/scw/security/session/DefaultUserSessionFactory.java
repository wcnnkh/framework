package scw.security.session;

import scw.data.cache.MemcachedTemporaryCache;
import scw.data.cache.RedisTemporaryCache;
import scw.data.cache.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public final class DefaultUserSessionFactory<T> extends AbstractUserSessionFactory<T> {
	private static final String PREFIX = "user_sid:";
	private String prefix;
	private TemporaryCache temporaryCache;
	private SessionFactory sessionFactory;

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, String prefix, TemporaryCache temporaryCache) {
		this.prefix = prefix;
		this.sessionFactory = new DefaultSessionFactory(defaultMaxInactiveInterval, prefix, temporaryCache);
		this.temporaryCache = temporaryCache;
	}

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, String prefix, Memcached memcached) {
		this(defaultMaxInactiveInterval, prefix, new MemcachedTemporaryCache(memcached));
	}

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, String prefix, Redis redis) {
		this(defaultMaxInactiveInterval, prefix, new RedisTemporaryCache(redis));
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected String getKey(String key) {
		return prefix == null ? (PREFIX + key) : (PREFIX + prefix + key);
	}

	@Override
	protected void invalidate(T uid) {
		String sessionId = getSessionId(uid);
		if (sessionId != null) {
			temporaryCache.delete(getKey(sessionId));
		}
		temporaryCache.delete(getKey(uid.toString()));
	}

	@Override
	protected void touch(T uid, Session session) {
		temporaryCache.touch(getKey(uid.toString()), session.getMaxInactiveInterval());
		temporaryCache.touch(getKey(session.getId()), session.getMaxInactiveInterval());
	}

	@Override
	protected void addUidMapping(T uid, Session session) {
		temporaryCache.set(getKey(uid.toString()), session.getMaxInactiveInterval(), session.getId());
		temporaryCache.set(getKey(session.getId()), session.getMaxInactiveInterval(), uid);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T getUid(String sessionId) {
		return (T) temporaryCache.get(getKey(sessionId));
	}

	@Override
	protected String getSessionId(T uid) {
		return (String) temporaryCache.get(getKey(uid.toString()));
	}
}