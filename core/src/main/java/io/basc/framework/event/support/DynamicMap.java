package io.basc.framework.event.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.basc.framework.event.ChangeType;
import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.event.observe.ObservableChangeEvent;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;

public class DynamicMap<K, V> {
	private final BroadcastEventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher;
	private final Map<K, V> targetMap;

	public DynamicMap() {
		this(new ConcurrentHashMap<>());
	}

	public DynamicMap(Map<K, V> targetMap) {
		this(targetMap, new StandardBroadcastEventDispatcher<>());
	}

	public DynamicMap(Map<K, V> targetMap, BroadcastEventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher) {
		Assert.isTrue(!CollectionUtils.isUnmodifiable(targetMap), "TargetMap Cannot be an Unmodifiable");
		Assert.requiredArgument(eventDispatcher != null, "");
		this.targetMap = targetMap;
		this.eventDispatcher = eventDispatcher;
	}

	private Map<K, V> compute(Entry<? extends K, ? extends V> entry,
			BiFunction<? super Entry<? extends K, ? extends V>, ? super Map<K, V>, ? extends V> function) {
		V oldValue = function.apply(entry, targetMap);
		if (oldValue != entry.getValue()) {
			ChangeType changeType;
			if (oldValue == null) {
				changeType = ChangeType.CREATE;
			} else if (entry.getValue() == null) {
				changeType = ChangeType.DELETE;
			} else {
				changeType = ChangeType.UPDATE;
			}
			Map<K, V> oldMap = Collections.singletonMap(entry.getKey(), oldValue);
			publishEvent(changeType, oldMap, Collections.singletonMap(entry.getKey(), entry.getValue()));
			return oldMap;
		}
		return Collections.emptyMap();
	}

	/**
	 * 操作键值对
	 * 
	 * @param key      被操作的key
	 * @param value    进行赋值操作时的值
	 * @param function
	 * @return
	 */
	public final V compute(K key, V value, Function<? super Map<K, V>, ? extends V> function) {
		Map<K, V> map = compute(Collections.singletonMap(key, value), null, (e, v) -> function.apply(v));
		if (map.isEmpty()) {
			return null;
		}

		for (Entry<K, V> entry : map.entrySet()) {
			return entry.getValue();
		}
		return null;
	}

	/**
	 * 对键值对批量操作
	 * 
	 * @param sourceMap
	 * @param changeType 操作类型,如果为空自动判断, 但为空的时候不会批量发送事件
	 * @param function
	 * @return
	 */
	public Map<K, V> compute(Map<? extends K, ? extends V> sourceMap, @Nullable ChangeType changeType,
			BiFunction<? super Entry<? extends K, ? extends V>, ? super Map<K, V>, ? extends V> function) {
		Assert.requiredArgument(function != null, "function");
		if (CollectionUtils.isEmpty(sourceMap)) {
			return Collections.emptyMap();
		}

		if (changeType == null) {
			if (sourceMap.size() == 1) {
				for (Entry<? extends K, ? extends V> entry : sourceMap.entrySet()) {
					Map<K, V> map = compute(entry, function);
					if (!map.isEmpty()) {
						return map;
					}
				}
				return Collections.emptyMap();
			} else {
				Map<K, V> oldMap = new LinkedHashMap<>(sourceMap.size());
				for (Entry<? extends K, ? extends V> entry : sourceMap.entrySet()) {
					Map<K, V> map = compute(entry, function);
					if (map.isEmpty()) {
						continue;
					}

					oldMap.putAll(map);
				}
				return Collections.unmodifiableMap(oldMap);
			}
		} else {
			if (sourceMap.size() == 1) {
				for (Entry<? extends K, ? extends V> entry : sourceMap.entrySet()) {
					V oldValue = function.apply(entry, targetMap);
					if (oldValue != entry.getValue()) {
						Map<K, V> oldMap = Collections.singletonMap(entry.getKey(), oldValue);
						publishEvent(changeType, oldMap, Collections.singletonMap(entry.getKey(), entry.getValue()));
						return oldMap;
					}
				}
				return Collections.emptyMap();
			} else {
				Map<K, V> oldMap = new LinkedHashMap<>(sourceMap.size());
				Map<K, V> newMap = new LinkedHashMap<>(sourceMap.size());
				for (Entry<? extends K, ? extends V> entry : sourceMap.entrySet()) {
					V oldValue = function.apply(entry, targetMap);
					if (oldValue != entry.getValue()) {
						oldMap.put(entry.getKey(), oldValue);
						newMap.put(entry.getKey(), entry.getValue());
					}
				}
				publishEvent(changeType, Collections.unmodifiableMap(oldMap), Collections.unmodifiableMap(newMap));
				return Collections.unmodifiableMap(oldMap);
			}
		}
	}

	public final BroadcastEventDispatcher<ObservableChangeEvent<Map<K, V>>> getEventDispatcher() {
		return eventDispatcher;
	}

	/**
	 * 这是一个不安全的对象,修改此对象会改变原始数据且不触发事件
	 * 
	 * @return
	 */
	public Map<K, V> getUnsafeMap() {
		return targetMap;
	}

	private void publishEvent(ChangeType changeType, Map<K, V> oldMap, Map<K, V> newMap) {
		ObservableChangeEvent<Map<K, V>> event = new ObservableChangeEvent<>(changeType, oldMap, newMap);
		eventDispatcher.publishEvent(event);
	}

	public final V put(K key, V value) {
		return compute(key, value, (map) -> map.put(key, value));
	}

	public final Map<K, V> putAll(Map<? extends K, ? extends V> map) {
		return compute(map, ChangeType.CREATE, (e, m) -> m.putIfAbsent(e.getKey(), e.getValue()));
	}

	public final V putIfAbsent(K key, V value) {
		return compute(key, value, (map) -> map.putIfAbsent(key, value));
	}

	public final V putIfPresent(K key, V value) {
		return compute(key, value, (map) -> map.computeIfPresent(key, (k, v) -> value));
	}

	public final V remove(K key) {
		return compute(key, null, (e) -> e.remove(key));
	}

	public final boolean remove(K key, V value) {
		return !removeAll(Collections.singletonMap(key, value)).isEmpty();
	}

	public final Map<K, V> removeAll(Iterable<? extends K> keys) {
		return removeAll(Elements.of(keys).toMap((e) -> e, (e) -> null));
	}

	public Map<K, V> removeAll(Map<? extends K, ? extends V> map) {
		if (CollectionUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		if (map.size() == 1) {
			for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
				if (entry.getValue() == null) {
					V oldValue = targetMap.remove(entry.getKey());
					if (oldValue == null) {
						Map<K, V> oldMap = Collections.singletonMap(entry.getKey(), oldValue);
						publishEvent(ChangeType.DELETE, oldMap, Collections.emptyMap());
						return oldMap;
					}
				} else {
					if (targetMap.remove(entry.getKey(), entry.getValue())) {
						publishEvent(ChangeType.DELETE, Collections.unmodifiableMap(map), Collections.emptyMap());
						return Collections.unmodifiableMap(map);
					}
				}
				break;
			}
			return Collections.emptyMap();
		} else {
			Map<K, V> oldMap = new LinkedHashMap<>(map.size());
			for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
				if (entry.getValue() == null) {
					V oldValue = targetMap.remove(entry.getKey());
					if (oldValue == null) {
						oldMap.put(entry.getKey(), oldValue);
					}
				} else {
					if (targetMap.remove(entry.getKey(), entry.getValue())) {
						oldMap.put(entry.getKey(), entry.getValue());
					}
				}
			}

			if (map.isEmpty()) {
				return Collections.emptyMap();
			}

			publishEvent(ChangeType.DELETE, Collections.unmodifiableMap(map), Collections.emptyMap());
			return Collections.unmodifiableMap(oldMap);
		}
	}
}
