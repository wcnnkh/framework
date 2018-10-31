package shuchaowen.core.db.storage.async;

import java.io.IOException;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedLock;
import shuchaowen.memcached.MemcachedQueue;

public final class MemcachedAsyncStorage extends AbstractAsyncStorage {
	private static final String CONSUMER_LOCK_KEY = "_consumer_lock";
	private MemcachedQueue memcachedQueue;

	public MemcachedAsyncStorage(AbstractDB db, final Memcached memcached, final String queueKey,
			AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
		memcachedQueue = new MemcachedQueue(queueKey, memcached);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						MemcachedLock memcachedLock = new MemcachedLock(memcached, queueKey + CONSUMER_LOCK_KEY);
						memcachedLock.lockWait(10);
						try {
							byte[] data = memcachedQueue.lockRead();
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
						} finally {
							memcachedLock.unLock();
						}
						Thread.sleep(10L);// 休眠一下，给其他服务出口竞争的机会
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
			memcachedQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Memcached getMemcached() {
		return memcachedQueue.getMemcached();
	}
}
