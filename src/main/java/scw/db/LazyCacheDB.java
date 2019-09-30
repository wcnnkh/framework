package scw.db;

import scw.core.Consumer;
import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.async.AsyncInfo;
import scw.db.cache.LazyCacheManager;
import scw.db.cache.MemcachedLazyCacheManager;
import scw.db.cache.RedisLazyCacheManager;
import scw.mq.MQ;
import scw.mq.support.MemcachedBlockingQueueMQ;
import scw.mq.support.MemoryBlockingQueueMQ;
import scw.mq.support.RedisBlockingQueueMQ;

public abstract class LazyCacheDB extends AbstractLazyCacheDB implements scw.core.Destroy, Consumer<AsyncInfo> {
	private static final String DEFAULT_ASYNC_NAME = LazyCacheDB.class.getName();
	private final boolean destroyMQ;

	public LazyCacheDB() {
		super(null, new MemoryBlockingQueueMQ<AsyncInfo>(true), DEFAULT_ASYNC_NAME);
		this.destroyMQ = true;
	};

	public LazyCacheDB(Memcached memcached) {
		this(memcached, null);
	}

	public LazyCacheDB(Redis redis) {
		this(redis, null);
	}

	public LazyCacheDB(Memcached memcached, String queueName) {
		this(memcached, null, queueName);
	}

	public LazyCacheDB(Redis redis, String queueName) {
		this(redis, null, queueName);
	}

	public LazyCacheDB(Memcached memcached, String cacheKeyPrefix, String queueName) {
		super(new MemcachedLazyCacheManager(memcached, cacheKeyPrefix),
				StringUtils.isEmpty(queueName) ? new MemoryBlockingQueueMQ<AsyncInfo>(true)
						: new MemcachedBlockingQueueMQ<AsyncInfo>(memcached, true),
				StringUtils.isEmpty(queueName) ? DEFAULT_ASYNC_NAME : queueName);
		this.destroyMQ = true;
	}

	public LazyCacheDB(Redis redis, String cacheKeyPrefix, String queueName) {
		super(new RedisLazyCacheManager(redis, cacheKeyPrefix),
				StringUtils.isEmpty(queueName) ? new MemoryBlockingQueueMQ<AsyncInfo>(true)
						: new RedisBlockingQueueMQ<AsyncInfo>(redis, true),
				StringUtils.isEmpty(queueName) ? DEFAULT_ASYNC_NAME : queueName);
		this.destroyMQ = true;
	}

	public LazyCacheDB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq, String queueName) {
		super(lazyCacheManager, mq, queueName);
		this.destroyMQ = false;
	}

	public void destroy() {
		if (destroyMQ) {
			MQ<AsyncInfo> mq = getMQ();
			if (mq != null && mq instanceof Destroy) {
				((Destroy) mq).destroy();
			}
		}
	}
}