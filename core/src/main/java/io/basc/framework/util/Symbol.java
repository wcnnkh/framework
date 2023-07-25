package io.basc.framework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.element.ElementList;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 符号/标记
 * <p>
 * 参考Level
 * 
 * @author wcnnkh
 *
 */
@ToString
@EqualsAndHashCode
public class Symbol implements Serializable, Named {
	private static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
	private static final long serialVersionUID = 1L;
	private static final TreeMap<Class<?>, Map<String, List<Symbol>>> SYMBOL_MAP = new TreeMap<>(
			TypeComparator.DEFAULT);

	private static void add(Symbol symbol) {
		WriteLock lock = READ_WRITE_LOCK.writeLock();
		lock.lock();
		try {
			Map<String, List<Symbol>> map = SYMBOL_MAP.get(symbol.getClass());
			if (map == null) {
				map = new HashMap<>();
				SYMBOL_MAP.put(symbol.getClass(), map);
			}

			List<Symbol> list = map.get(symbol.getName());
			if (list == null) {
				// 一般不会有太多
				list = new ArrayList<>(4);
				map.put(symbol.getName(), list);
			}

			list.add(symbol);
		} finally {
			lock.unlock();
		}
	}

	public static <T> T getOrCreate(Supplier<? extends T> supplier, Supplier<T> creator) {
		Assert.requiredArgument(supplier != null, "supplier");
		Assert.requiredArgument(creator != null, "creator");
		T value = supplier.get();
		if (value == null) {
			WriteLock lock = READ_WRITE_LOCK.writeLock();
			lock.lock();
			try {
				value = supplier.get();
				if (value == null) {
					value = creator.get();
				}
			} finally {
				lock.unlock();
			}
		}
		return value;
	}

	public static <T> T getFirstOrCreate(String name, Class<T> type, Supplier<T> creator) {
		Assert.requiredArgument(name != null, "name");
		return getOrCreate(() -> getSymbols(type, name).first(), creator);
	}

	public static Elements<Symbol> getSymbols() {
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			List<Symbol> symbols = SYMBOL_MAP.values().stream()
					.flatMap((map) -> map.values().stream().flatMap((list) -> list.stream()))
					.collect(Collectors.toList());
			return new ElementList<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> getSymbols(Class<? extends T> type) {
		Assert.requiredArgument(type != null, "type");
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			SortedMap<Class<?>, Map<String, List<Symbol>>> tailMap = SYMBOL_MAP.tailMap(type);
			if (tailMap == null) {
				return Elements.empty();
			}

			List<T> symbols = (List<T>) tailMap.values().stream()
					.flatMap((map) -> map.values().stream().flatMap((list) -> list.stream()))
					.collect(Collectors.toList());
			return new ElementList<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> getSymbols(Class<? extends T> type, String name) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(name != null, "name");
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			SortedMap<Class<?>, Map<String, List<Symbol>>> tailMap = SYMBOL_MAP.tailMap(type);
			if (tailMap == null) {
				return Elements.empty();
			}

			List<T> symbols = (List<T>) tailMap.values().stream().flatMap((map) -> {
				List<Symbol> list = map.get(name);
				if (list == null) {
					return Stream.empty();
				}
				return list.stream();
			}).collect(Collectors.toList());
			return new ElementList<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	public static Elements<Symbol> getSymbols(String name) {
		Assert.requiredArgument(name != null, "name");
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			List<Symbol> symbols = SYMBOL_MAP.values().stream().flatMap((map) -> {
				List<Symbol> list = map.get(name);
				if (list == null) {
					return Stream.empty();
				}
				return list.stream();
			}).collect(Collectors.toList());
			return new ElementList<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	public static Elements<String> keys() {
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			Set<String> symbols = SYMBOL_MAP.values().stream().flatMap((map) -> map.keySet().stream())
					.collect(Collectors.toSet());
			return new ElementSet<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	public static Elements<String> keys(Class<?> type) {
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			SortedMap<Class<?>, Map<String, List<Symbol>>> tailMap = SYMBOL_MAP.tailMap(type);
			if (tailMap == null) {
				return Elements.empty();
			}

			Set<String> symbols = tailMap.values().stream().flatMap((map) -> map.keySet().stream())
					.collect(Collectors.toSet());
			return new ElementSet<>(symbols);
		} finally {
			lock.unlock();
		}
	}

	public static Elements<Class<?>> types() {
		ReadLock lock = READ_WRITE_LOCK.readLock();
		lock.lock();
		try {
			Set<Class<?>> types = SYMBOL_MAP.keySet();
			return new ElementSet<>(types);
		} finally {
			lock.unlock();
		}
	}

	private final String name;

	public Symbol(String name) {
		Assert.requiredArgument(name != null, "name");
		this.name = name;
		add(this);
	}

	public final String getName() {
		return name;
	}
}
