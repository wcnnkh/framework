package scw.db;

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

public abstract class DB extends AbstractDB implements scw.core.Destroy {
	private final boolean destroyMQ;

	public DB() {
		super(null, new MemoryBlockingQueueMQ<AsyncInfo>(true), DB.class.getName());
		this.destroyMQ = true;
	};

	public DB(Memcached memcached) {
		this(memcached, null);
	}

	public DB(Redis redis) {
		this(redis, null);
	}

	public DB(Memcached memcached, String queueName) {
		super(new MemcachedLazyCacheManager(memcached),
				StringUtils.isEmpty(queueName) ? new MemoryBlockingQueueMQ<AsyncInfo>(true)
						: new MemcachedBlockingQueueMQ<AsyncInfo>(memcached, true),
				StringUtils.isEmpty(queueName) ? DB.class.getName() : queueName);
		this.destroyMQ = true;
	}

	public DB(Redis redis, String queueName) {
		super(new RedisLazyCacheManager(redis),
				StringUtils.isEmpty(queueName) ? new MemoryBlockingQueueMQ<AsyncInfo>(true)
						: new RedisBlockingQueueMQ<AsyncInfo>(redis, true),
				StringUtils.isEmpty(queueName) ? DB.class.getName() : queueName);
		this.destroyMQ = true;
	}

	public DB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq, String queueName) {
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