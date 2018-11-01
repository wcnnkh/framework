package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.util.IOUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XTime;
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
	private boolean error = false;

	public RedisAsyncStorage(AbstractDB db, final Redis redis, final String queueKey, AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
		redisQueue = new RedisQueue(queueKey, redis);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						if(error){
							Logger.error(queueKey, "redis async error");
							Thread.sleep(5 * XTime.ONE_MINUTE);//休息5分钟再次警告
							continue;
						}
						
						RedisLock redisLock = new RedisLock(redis, queueKey + CONSUMER_LOCK_KEY);
						redisLock.lockWait(10);
						try {
							byte[] data = redisQueue.lockRead();
							if (data == null) {
								continue;
							}

							Collection<OperationBean> operationBeans = IOUtils.byteToJavaObject(data);
							if (operationBeans == null) {
								continue;
							}

							try {
								getAsyncConsumer().consumer(getDb(), operationBeans);
							} catch (Exception e) {
								error = true;
								e.printStackTrace();
							}
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

	public Redis getRedis() {
		return redisQueue.getRedis();
	}

	public void op(Collection<OperationBean> operationBean) {
		// 这里使用jdk的序列化方式 ，因为不会出现各种没有经历过的问题
		try {
			byte[] data = IOUtils.javaObjectToByte(operationBean);
			redisQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
