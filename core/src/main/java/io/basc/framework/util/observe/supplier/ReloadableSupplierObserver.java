package io.basc.framework.util.observe.supplier;

import java.util.function.Supplier;

import io.basc.framework.util.Reloadable;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.ChangeType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReloadableSupplierObserver<T> implements Supplier<T>, Reloadable {
	@NonNull
	private final Supplier<? extends T> supplier;
	@NonNull
	private final EventPublishService<ChangeEvent<T>> eventPublishService;
	private volatile T cache;

	@Override
	public void reload() {
		reload(true);
	}

	public boolean reload(boolean forceUpdate) {
		if (cache == null || forceUpdate) {
			synchronized (this) {
				if (cache == null || forceUpdate) {
					T old = cache;
					cache = supplier.get();
					touchEvent(old, cache);
					return true;
				}
			}
		}
		return false;
	}

	private void touchEvent(T parent, T current) {
		if (parent == current) {
			return;
		}

		ChangeEvent<T> changeEvent;
		if (parent == null) {
			changeEvent = new ChangeEvent<>(current, ChangeType.CREATE);
		} else if (current == null) {
			changeEvent = new ChangeEvent<>(parent, ChangeType.DELETE);
		} else {
			changeEvent = new ChangeEvent<T>(current, parent);
		}
		eventPublishService.publishEvent(changeEvent);
	}

	@Override
	public T get() {
		if (cache == null) {
			reload(false);
		}
		return cache;
	}
}
