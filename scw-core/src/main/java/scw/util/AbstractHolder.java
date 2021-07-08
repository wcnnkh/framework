package scw.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class AbstractHolder<T> implements Holder<T> {
	private final AtomicBoolean active = new AtomicBoolean(true);
	private final Supplier<T> supplier;
	
	public AbstractHolder(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public boolean isActive() {
		return active.get();
	}

	@Override
	public T get() {
		return supplier.get();
	}

	@Override
	public boolean release() {
		if (active.compareAndSet(active.get(), false)) {
			return releeaseInternal();
		}
		return false;
	}

	protected abstract boolean releeaseInternal();
}
