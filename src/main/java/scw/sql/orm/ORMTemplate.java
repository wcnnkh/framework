package scw.sql.orm;

import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.cache.Cache;
import scw.sql.orm.cache.MemcachedCache;
import scw.sql.orm.cache.RedisCache;

public abstract class ORMTemplate extends AbstractORMCacheTemplate {
	public ORMTemplate(SqlFormat sqlFormat) {
		this(sqlFormat, null);
	}

	/**
	 * @param sqlFormat
	 * @param memcached
	 * @param exp
	 *            缓存过期时间(秒)
	 */
	public ORMTemplate(SqlFormat sqlFormat, Memcached memcached, int exp) {
		this(sqlFormat, new MemcachedCache(memcached, exp));
	}

	/**
	 * @param sqlFormat
	 * @param redis
	 * @param exp
	 *            缓存过期时间(秒)
	 */
	public ORMTemplate(SqlFormat sqlFormat, Redis redis, int exp) {
		this(sqlFormat, new RedisCache(redis, exp));
	}

	public ORMTemplate(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat, cache);
	}
}
