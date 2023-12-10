package io.basc.framework.observe;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存轮询
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class VariableObserver<E> extends Observer<E> implements Variable {
	private final AtomicLong atomicLastModified = new AtomicLong();

	public AtomicLong getAtomicLastModified() {
		return atomicLastModified;
	}

	@Override
	public long lastModified() {
		return atomicLastModified.get();
	}

	public long setLastModified(long lastModified) {
		return this.atomicLastModified.getAndSet(lastModified);
	}

	public boolean setLastModified(long oldValue, long newValue) {
		return this.atomicLastModified.compareAndSet(oldValue, newValue);
	}
}
