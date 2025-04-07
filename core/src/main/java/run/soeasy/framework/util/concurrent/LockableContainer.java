package run.soeasy.framework.util.concurrent;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.concurrent.locks.NoOpLock;
import run.soeasy.framework.util.function.Supplier;

/**
 * 使用复制读来实现线程安全
 * 
 * @author shuchaowen
 *
 * @param <C>
 */
public class LockableContainer<C, X extends Throwable> implements ReadWriteLock {
	private volatile C container;
	@NonNull
	private final Supplier<? extends C, ? extends X> containerSource;
	/**
	 * 读写锁的实现
	 */
	private volatile ReadWriteLock readWriteLock;

	public LockableContainer(@NonNull Supplier<? extends C, ? extends X> containerSource) {
		this.containerSource = containerSource;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof LockableContainer) {
			LockableContainer<?, ?> other = (LockableContainer<?, ?>) obj;
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
		return readAsInt((conainer) -> ObjectUtils.hashCode(conainer));
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
	protected C newContainer() throws X {
		return containerSource.get();
	}

	public void reset() {
		Lock lock = writeLock();
		try {
			lock.lock();
			if (container == null) {
				return;
			}
			container = null;
		} finally {
			lock.unlock();
		}
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

	public <E> List<E> readAsList(Function<? super C, ? extends List<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			List<E> elements = reader.apply(container);
			return shared(elements);
		} finally {
			lock.unlock();
		}
	}

	protected <T> List<T> shared(List<T> list) {
		if (list == null) {
			return null;
		}

		if (list.isEmpty()) {
			return list;
		}

		if (list.getClass().getName().startsWith("java.util.")) {
			return list;
		}

		return isThreadSafe() ? list.stream().collect(Collectors.toList()) : list;
	}

	protected <K, V> Map<K, V> shared(Map<K, V> map) {
		if (map == null) {
			return null;
		}

		if (map.isEmpty()) {
			return map;
		}

		if (map.getClass().getName().startsWith("java.util.")) {
			return map;
		}
		return isThreadSafe() ? new LinkedHashMap<>(map) : map;
	}

	protected <T> Set<T> shared(Set<T> set) {
		if (set == null) {
			return null;
		}

		if (set.isEmpty()) {
			return set;
		}
		
		if (set.getClass().getName().startsWith("java.util.")) {
			return set;
		}

		return isThreadSafe() ? set.stream().collect(Collectors.toSet()) : set;
	}

	public <E> List<E> writeAsList(Function<? super C, ? extends List<E>> writer) throws X {
		return write((c) -> {
			List<E> elements = writer.apply(c);
			return shared(elements);
		});
	}

	public <E> List<E> updateAsList(Function<? super C, ? extends List<E>> executor) {
		return update((c) -> {
			List<E> elements = executor.apply(c);
			return shared(elements);
		});
	}

	public <K, V> Map<K, V> readAsMap(Function<? super C, ? extends Map<K, V>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Map<K, V> map = reader.apply(container);
			return shared(map);
		} finally {
			lock.unlock();
		}
	}

	public <E> Set<E> readAsSet(Function<? super C, ? extends Set<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Set<E> elements = reader.apply(container);
			return shared(elements);
		} finally {
			lock.unlock();
		}
	}

	protected <T> Elements<T> shared(Elements<T> elements) {
		if (elements == null) {
			return null;
		}

		if (isThreadSafe()) {
			if (elements.isEmpty()) {
				return Elements.empty();
			}

			if (elements instanceof Collection) {
				List<T> list = elements.collect(Collectors.toList());
				return Elements.of(list);
			}
			return elements.toList();
		}
		return elements;
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
			return shared(elements);
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
	public int readAsInt(ToIntFunction<? super C> reader) {
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
	public <R> R write(Function<? super C, R> writer) throws X {
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
