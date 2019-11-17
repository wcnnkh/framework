package scw.db.support;

import java.util.Map;

import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.core.utils.XUtils;
import scw.data.TransactionContextCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.AsyncExecute;
import scw.db.cache.CacheManager;
import scw.db.cache.DefaultCacheManager;
import scw.mq.queue.MemoryQueue;
import scw.mq.queue.Queue;

@SuppressWarnings("rawtypes")
public final class DruidDBConfig extends AbstractDruidDBConfig {
	private CacheManager cacheManager;
	private Queue<AsyncExecute> asyncQueue;

	public DruidDBConfig(@ResourceParameter(DEFAULT_CONFIG) String properties) {
		super(ResourceUtils.getProperties(properties));
		this.cacheManager = new DefaultCacheManager(
				new TransactionContextCache());
		this.asyncQueue = new MemoryQueue<AsyncExecute>();
	}

	public DruidDBConfig(@ResourceParameter(DEFAULT_CONFIG) String properties,
			Memcached memcached) {
		this(ResourceUtils.getProperties(properties), memcached);
	}

	public DruidDBConfig(@ResourceParameter(DEFAULT_CONFIG) String properties,
			Redis redis) {
		this(ResourceUtils.getProperties(properties), redis);
	}

	public DruidDBConfig(Map properties, Memcached memcached) {
		super(properties);
		this.cacheManager = createCacheManager(properties, memcached);
		this.asyncQueue = createAsyncQueue(properties, memcached);
	}

	public DruidDBConfig(Map properties, Redis redis) {
		super(properties);
		this.cacheManager = createCacheManager(properties, redis);
		this.asyncQueue = createAsyncQueue(properties, redis);
	}

	public Queue<AsyncExecute> getAsyncQueue() {
		return asyncQueue;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public void destroy() {
		XUtils.destroy(asyncQueue);
		super.destroy();
	}
}
