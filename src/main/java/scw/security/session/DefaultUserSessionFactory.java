package scw.security.session;

import scw.core.annotation.DefaultValue;
import scw.core.annotation.Order;
import scw.core.annotation.ParameterName;
import scw.data.cache.CacheService;
import scw.data.cache.MemcachedCacheService;
import scw.data.cache.RedisCacheService;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public final class DefaultUserSessionFactory<T> extends AbstractUserSessionFactory<T> {
	private CacheService cacheService;
	private SessionFactory sessionFactory;

	@Order
	public DefaultUserSessionFactory(
			@ParameterName("user.session.timeout") @DefaultValue((86400 * 7) + "") int defaultMaxInactiveInterval,
			CacheService cacheService) {
		this.sessionFactory = new DefaultSessionFactory(defaultMaxInactiveInterval, cacheService);
		this.cacheService = cacheService;
	}

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, Memcached memcached) {
		this(defaultMaxInactiveInterval, new MemcachedCacheService(memcached));
	}

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, Redis redis) {
		this(defaultMaxInactiveInterval, new RedisCacheService(redis));
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected String getKey(String key) {
		return "user-session-factory:" + key;
	}

	@Override
	protected void invalidate(T uid) {
		String sessionId = getSessionId(uid);
		if (sessionId != null) {
			cacheService.delete(getKey(sessionId));
		}
		cacheService.delete(getKey(uid.toString()));
	}

	@Override
	protected void touch(T uid, Session session) {
		cacheService.touch(getKey(uid.toString()), session.getMaxInactiveInterval());
		cacheService.touch(getKey(session.getId()), session.getMaxInactiveInterval());
	}

	@Override
	protected void addUidMapping(T uid, Session session) {
		cacheService.set(getKey(uid.toString()), session.getMaxInactiveInterval(), session.getId());
		cacheService.set(getKey(session.getId()), session.getMaxInactiveInterval(), uid);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T getUid(String sessionId) {
		return (T) cacheService.get(getKey(sessionId));
	}

	@Override
	protected String getSessionId(T uid) {
		return (String) cacheService.get(getKey(uid.toString()));
	}
}
