package shuchaowen.core.db.storage.async;

import java.io.IOException;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisLock;
import shuchaowen.redis.RedisQueue;

/**
 * 使用redis的队列实现异步存盘,该方案可用于集群
 * 
 * @author shuchaowen
 *
 */
public final class RedisAsyncStorage extends AbstractAsyncStorage {
	private static final String CONSUMER_LOCK_KEY = "_consumer_lock";
	private RedisQueue redisQueue;

	public RedisAsyncStorage(AbstractDB db, final Redis redis, final String queueKey, AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
		redisQueue = new RedisQueue(queueKey, redis);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						RedisLock redisLock = new RedisLock(redis, queueKey + CONSUMER_LOCK_KEY);
						redisLock.lockWait(10);
						try {
							byte[] data = redisQueue.lockRead();
							if (data == null) {
								continue;
							}

							ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
							if (executeInfo == null) {
								continue;
							}

							getAsyncConsumer().consumer(getDb(), executeInfo);
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
