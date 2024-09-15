package io.basc.framework.observe.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.event.ChangeType;

public class StandardObservableMap<K, V> extends AbstractObservableMap<K, V> {
	private final ReadWriteLock readWriteLock;
	private final Map<K, V> targetMap;

	public StandardObservableMap() {
		this(new LinkedHashMap<>());
	}

	public StandardObservableMap(Map<K, V> targetMap) {
		this(targetMap, new ReentrantReadWriteLock());
	}

	public StandardObservableMap(Map<K, V> targetMap, ReadWriteLock readWriteLock) {
		Assert.requiredArgument(targetMap != null, "map");
		Assert.requiredArgument(readWriteLock != null, "readWriteLock");
		this.targetMap = targetMap;
		this.readWriteLock = readWriteLock;
	}

	@Override
	public void clear() {
		Lock lock = readWriteLock.writeLock();
		try {
			Map<K, V> backMap = new LinkedHashMap<>(targetMap);
			targetMap.clear();
			List<PropertyChangeEvent<K, V>> changeEvents = new ArrayList<>(backMap.size());
			for (Entry<K, V> entry : backMap.entrySet()) {
				changeEvents.add(new PropertyChangeEvent<>(this, ChangeType.DELETE, entry.getKey(), entry.getValue()));
			}
			publishBatchEvent(Elements.of(changeEvents));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return targetMap.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return targetMap.containsValue(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return new LinkedHashSet<>(Collections.unmodifiableMap(targetMap).entrySet());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public V get(Object key) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return targetMap.get(key);
		} finally {
			lock.unlock();
		}
	}

	private ChangeType getPutChangeType(K key) {
		if (targetMap.containsKey(key)) {
			return ChangeType.UPDATE;
		} else {
			return ChangeType.CREATE;
		}
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	public Map<K, V> getTargetMap() {
		return targetMap;
	}

	@Override
	public boolean isEmpty() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return targetMap.isEmpty();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> keySet() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return new LinkedHashSet<>(targetMap.keySet());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public V put(K key, V value) {
		Lock lock = readWriteLock.writeLock();
		try {
			ChangeType changeType = getPutChangeType(key);
			V oldValue = targetMap.put(key, value);
			publishEvent(new PropertyChangeEvent<>(this, changeType, key,
					changeType == ChangeType.UPDATE ? oldValue : value));
			return oldValue;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Lock lock = readWriteLock.writeLock();
		try {
			List<PropertyChangeEvent<K, V>> events = new ArrayList<>(m.size());
			for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
				ChangeType changeType = getPutChangeType(entry.getKey());
				V oldValue = targetMap.put(entry.getKey(), entry.getValue());
				events.add(new PropertyChangeEvent<>(this, changeType, entry.getKey(),
						changeType == ChangeType.UPDATE ? oldValue : entry.getValue()));
			}
			publishBatchEvent(Elements.of(events));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public V remove(Object key) {
		Lock lock = readWriteLock.writeLock();
		try {
			// 确实很影响性能
			Set<K> changeKeys = targetMap.keySet();
			V oldValue = targetMap.remove(key);
			Set<K> newKeys = targetMap.keySet();
			changeKeys.removeAll(newKeys);
			for (K changeKey : changeKeys) {
				publishEvent(new PropertyChangeEvent<>(this, ChangeType.DELETE, changeKey, oldValue));
			}
			return oldValue;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int size() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return targetMap.size();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Collection<V> values() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return new ArrayList<>(targetMap.values());
		} finally {
			lock.unlock();
		}
	}
}
