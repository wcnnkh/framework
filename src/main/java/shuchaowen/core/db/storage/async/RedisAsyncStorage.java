package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.util.Collection;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.cache.RedisQueue;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;

/**
 * 使用redis的队列实现异步存盘,该方案可用于集群
 * 
 * @author shuchaowen
 *
 */
public final class RedisAsyncStorage extends AbstractAsyncStorage {
	private RedisQueue redisQueue;
	private final boolean sqlDebug;

	public RedisAsyncStorage(AbstractDB db, Redis redis, String queueKey) {
		this(db, redis, queueKey, true);
	}

	public RedisAsyncStorage(AbstractDB db, final Redis redis, final String queueKey, final boolean sqlDebug) {
		super(db);
		this.sqlDebug = sqlDebug;
		redisQueue = new RedisQueue(queueKey, redis);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						byte[] data = redisQueue.lockReadWait(100);
						try {
							ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
							next(executeInfo);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Thread.sleep(10L);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
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
			redisQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Redis getRedis() {
		return redisQueue.getRedis();
	}
}
