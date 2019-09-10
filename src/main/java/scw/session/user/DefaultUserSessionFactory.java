package scw.session.user;

import scw.data.cache.MemcachedTemporaryCache;
import scw.data.cache.RedisTemporaryCache;
import scw.data.cache.TemporaryCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.session.DefaultSessionFactory;
import scw.session.SessionFactory;

public class DefaultUserSessionFactory<T> extends AbstractUserSessionFactory<T> {
	private TemporaryCache temporaryCache;
	private SessionFactory sessionFactory;

	public DefaultUserSessionFactory(int defaultMaxInactiveInterval, String prefix, TemporaryCache temporaryCache) {
		super(prefix);
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

	@Override
	public void touch(String key, int maxInactiveInterval) {
		temporaryCache.touch(key, maxInactiveInterval);
	}

	@Override
	public void set(String key, int maxInactiveInterval, Object value) {
		temporaryCache.set(key, maxInactiveInterval, value);
	}

	@Override
	public Object get(String key) {
		return temporaryCache.get(key);
	}

	@Override
	public Object getAndTouch(String key, int maxInactiveInterval) {
		return temporaryCache.getAndTouch(key, maxInactiveInterval);
	}

}
