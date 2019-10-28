package scw.data.cache.memory;

import java.util.concurrent.atomic.AtomicLong;

import scw.data.cache.AbstractMemoryCache;

public class CounterMemoryCache extends AbstractMemoryCache {
	private AtomicLong value = new AtomicLong();

	public long incr(long delta, long initialValue) {
		long prev, v;
		do {
			prev = value.get();
			v = isExpire(System.currentTimeMillis()) ? initialValue
					: (prev + delta);
		} while (!value.compareAndSet(prev, v));
		return v;
	}

	public long incr(long delta) {
		return incr(delta, 0);
	}

	public long decr(long delta, long initialValue) {
		return incr(-delta, initialValue);
	}

	public Object get() {
		return value.get();
	}

	public void set(Object value) {
		this.value.set((Long) value);
	}

	public long decr(long delta) {
		return incr(-delta);
	}
}
