package io.basc.framework.event.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventTypes;
import io.basc.framework.event.NamedEventRegistry;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Registration;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class ObservableMap<K, V> extends AbstractObservable<Map<K, V>>
		implements EventDispatcher<ObservableChangeEvent<Map<K, V>>>, NamedEventRegistry<K, ChangeEvent<K>> {
	private final Map<K, V> targetMap;
	private final EventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher;

	public ObservableMap() {
		this(new ConcurrentHashMap<>());
	}

	public ObservableMap(Map<K, V> targetMap) {
		this(targetMap, new SimpleEventDispatcher<>());
	}

	public ObservableMap(Map<K, V> targetMap, EventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher) {
		Assert.isTrue(!CollectionUtils.isUnmodifiable(targetMap), "TargetMap Cannot be an Unmodifiable");
		Assert.requiredArgument(eventDispatcher != null, "eventDispatcher");
		this.targetMap = targetMap;
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<Map<K, V>>> eventListener) {
		return eventDispatcher.registerListener(eventListener);
	}

	@Override
	public void publishEvent(ObservableChangeEvent<Map<K, V>> event) {
		eventDispatcher.publishEvent(event);
	}

	public Map<K, V> getTargetMap() {
		return Collections.unmodifiableMap(targetMap);
	}

	@Override
	protected Map<K, V> getValue() {
		return targetMap.isEmpty() ? null : targetMap;
	}

	public V put(K key, V value) {
		V oldValue = targetMap.put(key, value);
		if (oldValue != value) {
			publishEvent(new ObservableChangeEvent<Map<K, V>>(oldValue == null ? EventTypes.CREATE : EventTypes.UPDATE,
					Collections.singletonMap(key, oldValue), Collections.singletonMap(key, value)));
		}
		return oldValue;
	}

	public V putIfAbsent(K key, V value) {
		V oldValue = targetMap.putIfAbsent(key, value);
		if (oldValue == null && oldValue != value) {
			publishEvent(new ObservableChangeEvent<Map<K, V>>(EventTypes.CREATE, null,
					Collections.singletonMap(key, value)));
		}
		return oldValue;
	}

	public V putIfPresent(K key, V value) {
		V oldValue = targetMap.computeIfPresent(key, (k, v) -> value);
		if (oldValue != null && oldValue != value) {
			publishEvent(new ObservableChangeEvent<>(EventTypes.UPDATE, Collections.singletonMap(key, oldValue),
					Collections.singletonMap(key, value)));
		}
		return oldValue;
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		if (CollectionUtils.isEmpty(map)) {
			return;
		}

		Map<K, V> all = CollectionFactory.createApproximateMap(map, 16);
		all.putAll(map);
		targetMap.putAll(all);
		// 因为无法原子性的判断是否存在变更，所以直接认定为更新
		publishEvent(new ObservableChangeEvent<>(EventTypes.UPDATE, null, all));
	}

	public void clear() {
		Map<K, V> all = CollectionFactory.createApproximateMap(targetMap, 16);
		all.putAll(targetMap);
		Iterator<Entry<K, V>> iterator = all.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<K, V> entry = iterator.next();
			if (!targetMap.remove(entry.getKey(), entry.getValue())) {
				iterator.remove();
			}
		}
		publishEvent(new ObservableChangeEvent<>(EventTypes.DELETE, all, null));
	}

	public int size() {
		return targetMap.size();
	}

	public boolean isEmpty() {
		return targetMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return targetMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return targetMap.containsValue(value);
	}

	public V get(K key) {
		return targetMap.get(key);
	}

	public V remove(K key) {
		V value = targetMap.remove(key);
		if (value != null) {
			publishEvent(new ObservableChangeEvent<Map<K, V>>(EventTypes.DELETE, Collections.singletonMap(key, value),
					null));
		}
		return value;
	}

	public Set<K> keySet() {
		return getTargetMap().keySet();
	}

	public Collection<V> values() {
		return getTargetMap().values();
	}

	public Set<Entry<K, V>> entrySet() {
		return getTargetMap().entrySet();
	}

	@Override
	public Registration registerListener(K name, EventListener<ChangeEvent<K>> eventListener) {
		return registerListener((event) -> {
			Map<K, V> changeMap = event.getSource();
			if (CollectionUtils.isEmpty(changeMap)) {
				return;
			}

			StreamProcessorSupport.consumeAll(changeMap.entrySet().iterator(),
					(e) -> eventListener.onEvent(new ChangeEvent<>(event.getEventType(), e.getKey())));
		});
	}
}
