package scw.mq.queue;

import java.util.concurrent.TimeUnit;

import scw.data.memcached.Memcached;

public class MemcachedBlockingQueue<E> implements BlockingQueue<E> {
	private static final String READ_KEY = "_read";
	private static final String WRITE_KEY = "_write";
	private static final String WRITE_INDEX_KEY = "_write_index";

	private final Memcached memcached;
	private final String queueKey;

	public MemcachedBlockingQueue(Memcached memcached, String queueKey) {
		this.memcached = memcached;
		this.queueKey = queueKey;
	}

	private boolean checkCanRead(Long readIndex) {
		Long writeIndex = (Long) memcached.get(queueKey + WRITE_KEY);
		if (writeIndex == null) {
			return false;
		}

		if (readIndex == null) {
			return true;
		}

		if (writeIndex < 0) {
			if (readIndex >= 0) {
				return true;
			}
		}
		return writeIndex > readIndex;
	}

	public boolean offer(E e) {
		if (e == null) {
			throw new NullPointerException("MemcachedMQ not write null");
		}

		long writeIndex = memcached.incr(queueKey + WRITE_INDEX_KEY, 1);
		boolean b = memcached.add(queueKey + writeIndex, e);
		if (b) {
			memcached.set(queueKey + WRITE_KEY, writeIndex);
		}
		return b;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		boolean b = offer(e);
		if (b) {
			return true;
		}

		for (int i = 0; i < timeout; i++) {
			unit.sleep(1);
			b = offer(e);
			if (b) {
				return true;
			}
		}
		return false;
	}

	public E peek() {
		while (true) {
			Long readIndex = (Long) memcached.get(queueKey + READ_KEY);
			if (checkCanRead(readIndex)) {
				if (readIndex == null) {
					readIndex = 0L;
				} else {
					readIndex++;
				}

				@SuppressWarnings("unchecked")
				E message = (E) memcached.get(queueKey + readIndex);
				if (message == null) {
					memcached.set(queueKey + READ_KEY, readIndex);
					continue;
				}
				return message;
			} else {
				return null;
			}
		}
	}

	public E poll() {
		while (true) {
			Long readIndex = (Long) memcached.get(queueKey + READ_KEY);
			if (checkCanRead(readIndex)) {
				if (readIndex == null) {
					readIndex = 0L;
				} else {
					readIndex++;
				}

				@SuppressWarnings("unchecked")
				E message = (E) memcached.get(queueKey + readIndex);
				if (message == null) {
					memcached.set(queueKey + READ_KEY, readIndex);
					continue;
				}

				memcached.delete(queueKey + readIndex);
				memcached.set(queueKey + READ_KEY, readIndex);
				return message;
			} else {
				return null;
			}
		}
	}

	public void put(E e) throws InterruptedException {
		boolean b = offer(e);
		if (b) {
			return;
		}

		while (!b) {
			b = offer(e);
			Thread.sleep(0, 1);
		}
	}

	public E take() throws InterruptedException {
		E e = poll();
		while (e == null) {
			e = poll();
			Thread.sleep(0, 1);
		}
		return e;
	}
}
