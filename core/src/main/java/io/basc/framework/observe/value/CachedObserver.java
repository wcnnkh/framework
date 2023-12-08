package io.basc.framework.observe.value;

import java.util.concurrent.TimeUnit;

import io.basc.framework.observe.Pull;

public class CachedObserver<T extends Pull> extends Cached<T> {
	private final T observable;

	public CachedObserver(T observable) {
		this.observable = observable;
		if (observable != null) {
			getAtomicLastModified().set(observable.lastModified());
		}
	}

	@Override
	public T orElse(T other) {
		return observable == null ? other : observable;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return true;
	}

}
