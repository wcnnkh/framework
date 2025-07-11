package run.soeasy.framework.core.transmittable.registry;

import lombok.NonNull;
import run.soeasy.framework.core.transmittable.Inheriter;

public final class ThreadLocalInheriter<T> implements Inheriter<T, T> {
	private final ThreadLocal<T> threadLocal;
	// 是否可以向threadLocal中插入空值，默认为不可以
	private boolean nullable;

	public ThreadLocalInheriter(ThreadLocal<T> threadLocal) {
		this(threadLocal, false);
	}

	public ThreadLocalInheriter(@NonNull ThreadLocal<T> threadLocal, boolean nullable) {
		this.threadLocal = threadLocal;
		this.nullable = nullable;
	}

	public void set(T value) {
		// 如果不可以为空，那么应该删除
		if (!nullable && value == null) {
			threadLocal.remove();
		} else {
			threadLocal.set(value);
		}
	}

	@Override
	public T capture() {
		return threadLocal.get();
	}

	@Override
	public T replay(T capture) {
		T backup = threadLocal.get();
		set(capture);
		return backup;
	}

	@Override
	public void restore(T backup) {
		set(backup);
	}

	@Override
	public T clear() {
		T backup = threadLocal.get();
		threadLocal.remove();
		return backup;
	}

	@Override
	public String toString() {
		return threadLocal.toString();
	}

	@Override
	public int hashCode() {
		return threadLocal.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ThreadLocalInheriter) {
			ThreadLocalInheriter<?> inheriter = (ThreadLocalInheriter<?>) obj;
			return this.threadLocal.equals(inheriter.threadLocal);
		}
		return false;
	}
}
