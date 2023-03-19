package io.basc.framework.event.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventTypes;
import io.basc.framework.event.Observable;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Selector;

public class StandardObservable<T> extends AbstractObservable<T> implements EventDispatcher<ObservableChangeEvent<T>> {
	private final AtomicReference<T> valueReference = new AtomicReference<>();
	private final EventDispatcher<ObservableChangeEvent<T>> eventDispatcher;
	private volatile LinkedHashSet<Observable<? extends T>> sources;
	private Selector<T> selector;

	public StandardObservable() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public StandardObservable(EventDispatcher<ObservableChangeEvent<T>> eventDispatcher) {
		Assert.requiredArgument(eventDispatcher != null, "eventDispatcher");
		this.eventDispatcher = eventDispatcher;
	}

	public void setSelector(Selector<T> selector) {
		this.selector = selector;
		touch(null);
	}

	public final Selector<T> getSelector() {
		return selector;
	}

	public Registration registers(Iterator<? extends Observable<? extends T>> observables) {
		Assert.requiredArgument(observables != null, "observables");
		synchronized (this) {
			if (sources == null) {
				sources = new LinkedHashSet<>(8);
			}

			Registration registration = null;
			while (observables.hasNext()) {
				Observable<? extends T> observable = observables.next();
				if (observable == null || observable == this) {
					continue;
				}

				if (sources.add(observable)) {
					if (registration == null) {
						registration = Registration.EMPTY;
					}

					// 添加成功
					registration = registration.and(observable.registerListener((e) -> {
						touch(new ObservableChangeEvent<>(e, e.getOldSource(), e.getSource()));
					}));
					registration = registration.and(() -> {
						if (sources.remove(observable)) {
							touch(null);
						}
					});
				}
			}

			if (registration != null) {
				touch(null);
			}
			return registration == null ? Registration.EMPTY : registration;
		}
	}

	public Registration registers(Iterable<? extends Observable<? extends T>> observables) {
		Assert.requiredArgument(observables != null, "observables");
		return registers(observables.iterator());
	}

	public Registration register(Observable<? extends T> observable) {
		Assert.requiredArgument(observable != null, "observable");
		return registers(Arrays.asList(observable));
	}

	private void touch(ObservableChangeEvent<T> event) {
		// 如果只存在一个或没有source那么不需要刷新
		if (getSources().size() > 1) {
			set(select());
			return;
		}

		T value = valueReference.get();
		if (value == null) {
			// 没有值，那么可以直接push
			if (event == null) {
				// 不可能出现解绑到最后一个了还没有值
			} else {
				publishEvent(event);
			}
		} else {
			if (event == null) {
				publishEvent(new ObservableChangeEvent<T>(EventTypes.DELETE, value, select()));
			} else {
				publishEvent(event);
			}
		}
	}

	public final Set<Observable<? extends T>> getSources() {
		return sources == null ? Collections.emptySet() : Collections.unmodifiableSet(sources);
	}

	protected T select() {
		if (selector == null) {
			for (Observable<? extends T> observable : sources) {
				if (observable == null || observable == this) {
					continue;
				}

				T value = observable.orElse(null);
				if (value != null) {
					return value;
				}
			}
			return null;
		}

		List<T> values = new ArrayList<>(sources.size());
		for (Observable<? extends T> observable : sources) {
			if (observable == null || observable == this) {
				continue;
			}

			observable.ifPresent(values::add);
		}

		if (values.isEmpty()) {
			return null;
		}

		if (values.size() == 1) {
			return values.get(0);
		}

		return selector.apply(values);
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return eventDispatcher.registerListener(eventListener);
	}

	@Override
	public void publishEvent(ObservableChangeEvent<T> event) {
		eventDispatcher.publishEvent(event);
	}

	public AtomicReference<T> getValueReference() {
		return valueReference;
	}

	@Override
	protected T getValue() {
		T value = valueReference.get();
		if (value == null && getSources().size() == 1) {
			// 如果没有缓存值且只存在一个source那么无需使用valueReference缓存
			return Cursor.of(getSources()).first().orElse(null);
		}
		return value;
	}

	private void publishEvent(T oldValue, T newValue) {
		if (oldValue == newValue) {
			return;
		}

		if (oldValue == null) {
			if (newValue == null) {
				return;
			} else {
				// 创建
				publishEvent(new ObservableChangeEvent<>(EventTypes.CREATE, oldValue, newValue));
			}
		} else {
			if (newValue == null) {
				// 删除
				publishEvent(new ObservableChangeEvent<>(EventTypes.DELETE, oldValue, newValue));
			} else {
				// 更新
				publishEvent(new ObservableChangeEvent<>(EventTypes.UPDATE, oldValue, newValue));
			}
		}
	}

	public T set(T value) {
		T old = valueReference.getAndSet(value);
		publishEvent(old, value);
		return old;
	}

	public boolean set(T oldValue, T newValue) {
		if (oldValue == newValue) {
			return false;
		}

		if (valueReference.compareAndSet(oldValue, newValue)) {
			publishEvent(oldValue, newValue);
			return true;
		}
		return false;
	}
}
