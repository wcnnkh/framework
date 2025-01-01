package io.basc.framework.util.concurrent;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.locks.NoOpLock;
import lombok.NonNull;

/**
 * 使用复制读来实现线程安全
 * 
 * @author shuchaowen
 *
 * @param <C>
 */
public class CopyOnReaderContainer<C> implements ReadWriteLock {
	private volatile C container;
	@NonNull
	private final Supplier<? extends C> containerSupplier;
	/**
	 * 读写锁的实现
	 */
	private volatile ReadWriteLock readWriteLock;

	public CopyOnReaderContainer(@NonNull Supplier<? extends C> containerSupplier) {
		this.containerSupplier = containerSupplier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CopyOnReaderContainer) {
			CopyOnReaderContainer<?> other = (CopyOnReaderContainer<?>) obj;
			return readAsBoolean((o1) -> other.readAsBoolean((o2) -> ObjectUtils.equals(o1, o2)));
		}

		return readAsBoolean((conainer) -> ObjectUtils.equals(conainer, obj));
	}

	/**
	 * 执行，但不会从供应商获取container
	 * 
	 * @param executor 回调参数不会为空
	 * @return members为空会直接返回false, 或executor的返回值
	 */
	public boolean execute(Predicate<? super C> executor) {
		Lock lock = writeLock();
		lock.lock();
		try {
			if (container == null) {
				return false;
			}
			return executor.test(container);
		} finally {
			lock.unlock();
		}
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public int hashCode() {
		return readInt((conainer) -> ObjectUtils.hashCode(conainer));
	}

	/**
	 * 是否是线程安全的
	 * 
	 * @return
	 */
	public boolean isThreadSafe() {
		return readWriteLock != null;
	}

	/**
	 * 初始化容器时的回调
	 * 
	 * @return
	 */
	protected C newContainer() {
		return containerSupplier.get();
	}

	/**
	 * 读取
	 * 
	 * @param <R>
	 * @param reader 回调参数可能为空
	 * @return
	 */
	public <R> R read(Function<? super C, ? extends R> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			return reader.apply(container);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 测试container
	 * 
	 * @param predicate 回调参数可能为空
	 * @return
	 */
	public boolean readAsBoolean(Predicate<? super C> predicate) {
		Lock lock = readLock();
		lock.lock();
		try {
			return predicate.test(container);
		} finally {
			lock.unlock();
		}
	}

	public <E> List<E> readAsList(Function<? super C, ? extends Elements<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Elements<E> elements = reader.apply(container);
			if (elements == null) {
				return null;
			}

			return isThreadSafe() ? elements.collect(Collectors.toList()) : elements.toList();
		} finally {
			lock.unlock();
		}
	}

	public <K, V> Map<K, V> readAsMap(Function<? super C, ? extends Map<K, V>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Map<K, V> map = reader.apply(container);
			if (map == null || map.isEmpty()) {
				return map;
			}

			return isThreadSafe() ? new LinkedHashMap<>(map) : map;
		} finally {
			lock.unlock();
		}
	}

	public <E> Set<E> readAsSet(Function<? super C, ? extends Elements<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Elements<E> elements = reader.apply(container);
			if (elements == null) {
				return null;
			}

			return isThreadSafe() ? elements.collect(Collectors.toSet()) : elements.toSet();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 容器可能为空
	 * 
	 * @param <E>
	 * @param reader
	 * @return
	 */
	public <E> Elements<E> readAsElements(Function<? super C, ? extends Elements<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Elements<E> elements = reader.apply(container);
			if (elements == null) {
				return null;
			}

			if (isThreadSafe()) {
				if (elements.isEmpty()) {
					return Elements.empty();
				}

				if (elements instanceof Collection) {
					List<E> list = elements.collect(Collectors.toList());
					return Elements.of(list);
				}
				return elements.toList();
			}
			return elements;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 读取并返回一个int
	 * 
	 * @param reader 回调参数可能为空
	 * @return
	 */
	public int readInt(ToIntFunction<? super C> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			return reader.applyAsInt(container);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Lock readLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.readLock();
	}

	public void setReadWriteLock(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	@Override
	public String toString() {
		return read((e) -> e == null ? null : e.toString());
	}

	/**
	 * 执行，但不会从供应商获取container
	 * 
	 * @param <R>
	 * @param executor 回调参数可能为空
	 * @return
	 */
	public <R> R update(Function<? super C, R> executor) {
		Lock lock = writeLock();
		lock.lock();
		try {
			return executor.apply(container);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 写入，如果container为空会从供应商获取
	 * 
	 * @param <R>
	 * @param writer 回调参数不会为空
	 * @return
	 */
	public <R> R write(Function<? super C, R> writer) {
		Lock lock = writeLock();
		lock.lock();
		try {
			if (container == null) {
				container = newContainer();
			}
			return writer.apply(container);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Lock writeLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.writeLock();
	}
}
