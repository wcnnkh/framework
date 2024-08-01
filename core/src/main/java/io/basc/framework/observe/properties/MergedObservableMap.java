package io.basc.framework.observe.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import io.basc.framework.observe.register.ObservableRegistry;
import io.basc.framework.observe.value.ConvertibleObservableValue;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.select.MapMerger;
import io.basc.framework.util.select.Selector;

public class MergedObservableMap<K, V> extends StandardObservableMap<K, V> {
	private final ObservableRegistry<ObservableValue<? extends Map<K, V>>> registry = new ObservableRegistry<>();

	private Selector<Map<K, V>> selector = MapMerger.getSingleton();

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

	public Registration registerMap(ObservableMap<K, V> observableMap) {
		return registerValue(observableMap.asObservableValue());
	}

	public Registration registerValue(ObservableValue<? extends Map<K, V>> observableValue) {
		return registry.register(observableValue);
	}

	public <S> Registration registerValue(ObservableValue<? extends S> observableValue,
			Function<? super S, ? extends Map<K, V>> converter) {
		ConvertibleObservableValue<S, Map<K, V>> convertibleObservableValue = new ConvertibleObservableValue<>(
				observableValue, converter);
		return registerValue(convertibleObservableValue);
	}

	public void rfreshMap() {
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
		this.selector = selector == null ? MapMerger.getSingleton() : selector;
		rfreshMap();
	}
}
