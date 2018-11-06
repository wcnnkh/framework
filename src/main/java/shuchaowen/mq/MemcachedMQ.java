package shuchaowen.mq;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.XUtils;
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
		memcached.add(queueKey + READ_KEY, 0 + "");
		memcached.add(queueKey + WRITE_INDEX_KEY, 0 + "");

		thread = new Thread(new Runnable() {

			public void run() {
				boolean find = false;
				try {
					while (!Thread.interrupted()) {
						if (consumerList == null || consumerList.isEmpty()) {
							continue;
						}
						
						MemcachedLock memcachedLock = new MemcachedLock(memcached, queueKey + READ_LOCK_KEY,
								XUtils.getUUID(), 600 * consumerList.size());
						if (memcachedLock.lock()) {
							try {
								if (checkCanRead()) {
									long readIndex = memcached.incr(queueKey + READ_KEY, 1);
									T message = memcached.get(queueKey + readIndex);
									for (Consumer<T> consumer : consumerList) {
										consumer.handler(message);// 如果出现异常则会出现重复执行的情况
									}
									memcached.delete(queueKey + readIndex);
									find = true;
								}

							} catch (Exception e) {
								throw new ShuChaoWenRuntimeException(e);
							} finally {
								memcachedLock.unLock();
							}
						}

						if (!find) {
							Thread.sleep(100L);
						}
						find = false;
					}
				} catch (InterruptedException e) {
				}
			}
		}, queueKey);
		thread.start();
	}

	private boolean checkCanRead() {
		long readIndex = Integer.parseInt((String) memcached.get(queueKey + READ_KEY));
		Long writeIndex = memcached.get(queueKey + WRITE_KEY);
		if (writeIndex == null) {
			return false;
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
}
