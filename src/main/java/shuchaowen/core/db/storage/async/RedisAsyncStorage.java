package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;
import shuchaowen.core.util.RedisLock;

/**
 * 使用redis的队列实现异步存盘,该方案可用于集群
 * 
 * @author shuchaowen
 *
 */
public class RedisAsyncStorage extends AbstractAsyncStorage {
	private final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private final Redis redis;
	private final String queueKey;
	private final byte[] queueByteKey;
	private final boolean sqlDebug;

	public RedisAsyncStorage(AbstractDB db, Redis redis, String queueKey) {
		this(db, redis, queueKey, true);
	}

	public RedisAsyncStorage(AbstractDB db, final Redis redis, final String queueKey, final boolean sqlDebug) {
		super(db);
		this.sqlDebug = sqlDebug;
		this.redis = redis;
		this.queueKey = queueKey;
		this.queueByteKey = queueKey.getBytes(DEFAULT_CHARSET);
		new Thread(new Runnable() {

			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(100L);
						RedisLock redisLock = new RedisLock(redis, "lock_" + queueKey);
						redisLock.lockWait();
						try {
							byte[] data = redis.lpop(queueByteKey);
							if (data == null) {
								continue;
							}
							
							try {
								ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
								next(executeInfo);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							redisLock.unLock();
						}
						Thread.sleep(1L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void next(ExecuteInfo executeInfo) {
		if (executeInfo == null) {
			return;
		}

		Collection<SQL> sqls = getSqlList(executeInfo);
		if (sqlDebug) {
			for (SQL sql : sqls) {
				logger(sql);
			}
		}

		try {
			DBUtils.execute(getDb(), sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		// 这里使用jdk的序列化方式 ，因为不会出现各种没有经历过的问题
		try {
			byte[] data = IOUtils.javaObjectToByte(executeInfo);
			redis.lpush(queueByteKey, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getQueueKey() {
		return queueKey;
	}

	public byte[] getQueueByteKey() {
		return queueByteKey;
	}

	public Redis getRedis() {
		return redis;
	}
}
