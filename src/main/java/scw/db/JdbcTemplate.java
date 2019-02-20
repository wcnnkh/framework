package scw.db;

import scw.memcached.Memcached;
import scw.redis.Redis;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.cache.Cache;
import scw.sql.orm.cache.MemcachedCache;
import scw.sql.orm.cache.RedisCache;

public abstract class JdbcTemplate extends AbstractORMCacheTemplate {
 
	public JdbcTemplate(SqlFormat sqlFormat) {
		this(sqlFormat, null);
	}

	/**
	 * @param sqlFormat
	 * @param memcached
	 * @param exp
	 *            缓存过期时间(秒)
	 */
	public JdbcTemplate(SqlFormat sqlFormat, Memcached memcached, int exp) {
		this(sqlFormat, new MemcachedCache(memcached, exp));
	}

	/**
	 * @param sqlFormat
	 * @param redis
	 * @param exp
	 *            缓存过期时间(秒)
	 */
	public JdbcTemplate(SqlFormat sqlFormat, Redis redis, int exp) {
		this(sqlFormat, new RedisCache(redis, exp));
	}

	public JdbcTemplate(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat, cache);
	}
	
	
}
