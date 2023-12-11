package io.basc.framework.observe.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.register.ObservableRegistry;
import io.basc.framework.observe.value.ConvertibleObservableValue;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.MapCombiner;
import io.basc.framework.util.select.Selector;

public class MergedObservableMap<K, V> extends StandardObservableMap<K, V> {
	private static class ObservableMapWrapper<K, V> implements ObservableValue<Map<K, V>> {
		private final ObservableMap<K, V> observableMap;

		public ObservableMapWrapper(ObservableMap<K, V> observableMap) {
			this.observableMap = observableMap;
		}

		@Override
		public Map<K, V> orElse(Map<K, V> other) {
			return observableMap;
		}

		@Override
		public Registration registerBatchListener(BatchEventListener<ChangeEvent> batchEventListener)
				throws EventRegistrationException {
			return observableMap.registerBatchListener((events) -> batchEventListener.onEvent(events.map((e) -> e)));
		}
	}

	private final ObservableRegistry<ObservableValue<? extends Map<K, V>>> registry = new ObservableRegistry<>();

	private Selector<Map<K, V>> selector = MapCombiner.getSingleton();

	public MergedObservableMap() {
		this(new LinkedHashMap<>());
	}

	public MergedObservableMap(Map<K, V> targetMap) {
		super(targetMap);
	}

	public Elements<Map<K, V>> getMergedElmenets() {
		return registry.getServices().map((e) -> e.get());
	}

	public Map<K, V> getMergedMap() {
		Lock lock = getReadWriteLock().readLock();
		lock.lock();
		try {
			return selector.apply(getMergedElmenets().concat(Elements.singleton(getTargetMap())));
		} finally {
			lock.unlock();
		}
	}

	public ObservableRegistry<? extends ObservableValue<? extends Map<K, V>>> getRegistry() {
		return registry;
	}

	public Selector<Map<K, V>> getSelector() {
		return selector;
	}

	public Registration registerObservableMap(ObservableMap<K, V> observableMap) {
		return registerObservableValue(new ObservableMapWrapper<>(observableMap));
	}

	public Registration registerObservableValue(ObservableValue<? extends Map<K, V>> observableValue) {
		return registry.register(observableValue);
	}

	public <S> Registration registerObservableValue(ObservableValue<? extends S> observableValue,
			Function<? super S, ? extends Map<K, V>> converter) {
		ConvertibleObservableValue<S, Map<K, V>> convertibleObservableValue = new ConvertibleObservableValue<>(
				observableValue, converter);
		return registerObservableValue(convertibleObservableValue);
	}

	public void reload() {
		if (Selector.first().equals(selector)) {
			return;
		}

		Lock lock = getReadWriteLock().writeLock();
		lock.lock();
		try {
			getTargetMap().clear();
			putAll(getMergedMap());
		} finally {
			lock.unlock();
		}
	}

	public void setSelector(Selector<Map<K, V>> selector) {
		this.selector = selector == null ? MapCombiner.getSingleton() : selector;
		reload();
	}
}
