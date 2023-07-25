package io.basc.framework.util;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;

public class TypeRegistry<K, V> {
	private volatile TreeMap<Class<? extends K>, V> map;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public V register(Class<? extends K> key, V value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			if (map == null) {
				map = new TreeMap<>(TypeComparator.DEFAULT);
			}

			return map.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	public V unregister(Class<? extends K> key) {
		Assert.requiredArgument(key != null, "key");
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			if (map == null) {
				return null;
			}
			return map.remove(key);
		} finally {
			lock.unlock();
		}
	}

	public V get(Class<? extends K> key) {
		Assert.requiredArgument(key != null, "key");
		return read((map) -> map == null ? null : map.get(key));
	}

	public <T, E extends Throwable> T read(
			Processor<? super TreeMap<Class<? extends K>, V>, ? extends T, ? extends E> reader) throws E {
		Assert.requiredArgument(reader != null, "reader");
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return reader.process(map);
		} finally {
			lock.unlock();
		}
	}

	public <T, E extends Throwable> T write(
			Processor<? super TreeMap<Class<? extends K>, V>, ? extends T, ? extends E> writer) throws E {
		Assert.requiredArgument(writer != null, "reader");
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			if (map == null) {
				map = new TreeMap<>(TypeComparator.DEFAULT);
			}

			return writer.process(map);
		} finally {
			lock.unlock();
		}
	}

	public V find(Class<? extends K> key) {
		Assert.requiredArgument(key != null, "key");
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			if (map == null) {
				return null;
			}
			V value = map.get(key);
			if (value != null) {
				return value;
			}

			for (Entry<Class<? extends K>, V> entry : map.entrySet()) {
				if (entry.getKey().isAssignableFrom(key)) {
					return entry.getValue();
				}
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	public Elements<Class<? extends K>> getRegisteds() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			if (map == null) {
				return Elements.empty();
			}

			Set<Class<? extends K>> sets = map.keySet();
			return Elements.of(sets);
		} finally {
			lock.unlock();
		}
	}
}
