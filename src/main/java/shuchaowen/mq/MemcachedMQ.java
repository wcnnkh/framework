package shuchaowen.mq;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.XUtils;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedLock;

public final class MemcachedMQ<T> implements MQ<T> {
	private static final String READ_KEY = "_read";
	private static final String WRITE_KEY = "_write";
	private static final String WRITE_INDEX_KEY = "_write_index";
	private static final String READ_LOCK_KEY = "_read_lock";

	private final Memcached memcached;
	private final String queueKey;
	private volatile List<Consumer<T>> consumerList;
	private final Thread thread;

	public MemcachedMQ(final Memcached memcached, final String queueKey) {
		this.memcached = memcached;
		this.queueKey = queueKey;
		
		thread = new Thread(new Runnable() {

			public void run() {
				long sleepTime = 0L;
				try {
					while (!Thread.interrupted()) {
						if (consumerList == null || consumerList.isEmpty()) {
							continue;
						}
						
						MemcachedLock memcachedLock = new MemcachedLock(memcached, queueKey + READ_LOCK_KEY,
								XUtils.getUUID(), 600 * consumerList.size());
						if (memcachedLock.lock()) {
							try {
								Long readIndex = memcached.get(queueKey + READ_KEY);
								if (checkCanRead(readIndex)) {
									if(readIndex == null){
										readIndex = 0L;
									}else{
										readIndex ++;
									}
									T message = memcached.get(queueKey + readIndex);
									if(message == null){
										memcached.set(queueKey + READ_KEY, readIndex);
										continue;
									}
									
									for (Consumer<T> consumer : consumerList) {
										consumer.handler(message);// 如果出现异常则会出现重复执行的情况
									}
									memcached.delete(queueKey + readIndex);
									memcached.set(queueKey + READ_KEY, readIndex);
									sleepTime = 0;
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								memcachedLock.unLock();
							}
						}

						if (sleepTime > 0) {
							Thread.sleep(sleepTime);
						}
						sleepTime = Math.min(5000L, sleepTime + 100);
					}
				} catch (InterruptedException e) {
				}
			}
		}, queueKey);
	}

	private boolean checkCanRead(Long readIndex) {
		Long writeIndex = memcached.get(queueKey + WRITE_KEY);
		if (writeIndex == null) {
			return false;
		}
		
		if(readIndex == null){
			return true;
		}
		
		if (writeIndex < 0) {
			if (readIndex >= 0) {
				return true;
			}
		}
		return writeIndex > readIndex;
	}

	public void push(T message) {
		if (message == null) {
			throw new NullPointerException("MemcachedMQ not write null");
		}

		long writeIndex = memcached.incr(queueKey + WRITE_INDEX_KEY, 1);
		boolean b = memcached.add(queueKey + writeIndex, message);
		if (!b) {
			throw new ShuChaoWenRuntimeException("push error index" + writeIndex);
		}
		memcached.set(queueKey + WRITE_KEY, writeIndex);
	}

	public synchronized void consumer(Consumer<T> consumer) {
		if (consumerList == null) {
			consumerList = new ArrayList<Consumer<T>>();
		}
		consumerList.add(consumer);
	}

	public void destroy() {
		thread.interrupt();
	}

	public void start() {
		thread.start();
	}
}
