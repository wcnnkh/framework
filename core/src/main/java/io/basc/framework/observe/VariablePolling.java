package io.basc.framework.observe;

import java.util.concurrent.atomic.AtomicLong;

import io.basc.framework.util.actor.batch.BatchEventDispatcher;
import io.basc.framework.util.watch.Variable;
import lombok.NonNull;

/**
 * 缓存轮询
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public abstract class VariablePolling<E> extends PollingObserver<E> implements Variable {
	private final AtomicLong atomicLastModified = new AtomicLong();

	public VariablePolling(@NonNull BatchEventDispatcher<E> eventDispatcher) {
		super(eventDispatcher);
	}

	public AtomicLong getAtomicLastModified() {
		return atomicLastModified;
	}

	@Override
	public long lastModified() {
		return atomicLastModified.get();
	}

	public long setLastModified(long lastModified) {
		long oldValue = this.atomicLastModified.getAndSet(lastModified);
		modifyLastModified(oldValue, lastModified);
		return oldValue;
	}

	public boolean setLastModified(long oldValue, long newValue) {
		if (this.atomicLastModified.compareAndSet(oldValue, newValue)) {
			modifyLastModified(oldValue, newValue);
			return true;
		}
		return false;
	}

	protected abstract void modifyLastModified(long oldValue, long newValue);
}