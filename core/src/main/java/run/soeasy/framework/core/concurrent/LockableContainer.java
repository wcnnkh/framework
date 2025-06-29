package run.soeasy.framework.core.concurrent;

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
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.concurrent.locks.NoOpLock;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 使用复制读来实现线程安全
 * 
 * @author soeasy.run
 *
 * @param <C> 容器类型
 */
public class LockableContainer<C, X extends Throwable> implements ReadWriteLock {
	private volatile C container;
	@NonNull
	private final ThrowingSupplier<? extends C, ? extends X> containerSource;
	/**
	 * 读写锁的实现
	 */
	private volatile ReadWriteLock readWriteLock;

	/**
	 * 构造函数，初始化LockableContainer
	 * 
	 * @param containerSource 容器数据源，不能为null
	 */
	public LockableContainer(@NonNull ThrowingSupplier<? extends C, ? extends X> containerSource) {
		this.containerSource = containerSource;
	}

	/**
	 * 比较对象是否相等
	 * 
	 * @param obj 要比较的对象
	 * @return 如果对象相等则返回true，否则返回false
	 */
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

	/**
	 * 获取读写锁
	 * 
	 * @return 读写锁实例
	 */
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	/**
	 * 计算对象哈希值
	 * 
	 * @return 对象的哈希值
	 */
	@Override
	public int hashCode() {
		return readAsInt((conainer) -> ObjectUtils.hashCode(conainer));
	}

	/**
	 * 是否是线程安全的
	 * 
	 * @return 如果是线程安全的返回true，否则返回false
	 */
	public boolean isThreadSafe() {
		return readWriteLock != null;
	}

	/**
	 * 初始化容器时的回调
	 * 
	 * @return 新的容器实例
	 * @throws X 可能抛出的异常
	 */
	protected C newContainer() throws X {
		return containerSource.get();
	}

	/**
	 * 重置容器，将容器置为null
	 */
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
	 * 读取容器内容
	 * 
	 * @param <R>    返回值类型
	 * @param reader 读取容器内容的函数，回调参数可能为空
	 * @return 读取结果
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
	 * @param predicate 测试容器内容的断言函数，回调参数可能为空
	 * @return 测试结果
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

	/**
	 * 读取容器内容并转换为列表
	 * 
	 * @param <E>    列表元素类型
	 * @param reader 读取容器内容的函数
	 * @return 列表形式的容器内容
	 */
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

	/**
	 * 创建列表的共享副本，确保线程安全
	 * 
	 * @param <T>  列表元素类型
	 * @param list 原始列表
	 * @return 共享副本列表
	 */
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

	/**
	 * 创建映射的共享副本，确保线程安全
	 * 
	 * @param <K>  键类型
	 * @param <V>  值类型
	 * @param map  原始映射
	 * @return 共享副本映射
	 */
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

	/**
	 * 创建集合的共享副本，确保线程安全
	 * 
	 * @param <T>  集合元素类型
	 * @param set  原始集合
	 * @return 共享副本集合
	 */
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

	/**
	 * 写入并返回列表形式的容器内容
	 * 
	 * @param <E>    列表元素类型
	 * @param writer 写入容器内容的函数
	 * @return 列表形式的容器内容
	 * @throws X 可能抛出的异常
	 */
	public <E> List<E> writeAsList(Function<? super C, ? extends List<E>> writer) throws X {
		return write((c) -> {
			List<E> elements = writer.apply(c);
			return shared(elements);
		});
	}

	/**
	 * 更新并返回列表形式的容器内容
	 * 
	 * @param <E>      列表元素类型
	 * @param executor 更新容器内容的函数
	 * @return 列表形式的容器内容
	 */
	public <E> List<E> updateAsList(Function<? super C, ? extends List<E>> executor) {
		return update((c) -> {
			List<E> elements = executor.apply(c);
			return shared(elements);
		});
	}

	/**
	 * 读取容器内容并转换为映射
	 * 
	 * @param <K>    键类型
	 * @param <V>    值类型
	 * @param reader 读取容器内容的函数
	 * @return 映射形式的容器内容
	 */
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

	/**
	 * 读取容器内容并转换为集合
	 * 
	 * @param <E>    集合元素类型
	 * @param reader 读取容器内容的函数
	 * @return 集合形式的容器内容
	 */
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

	/**
	 * 创建Elements的共享副本，确保线程安全
	 * 
	 * @param <T>      元素类型
	 * @param elements 原始Elements
	 * @return 共享副本Elements
	 */
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
	 * @param <E>    元素类型
	 * @param reader 读取容器内容的函数
	 * @return Elements形式的容器内容
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
	 * @param reader 读取容器内容的函数，回调参数可能为空
	 * @return 整数值
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

	/**
	 * 获取读锁
	 * 
	 * @return 读锁实例
	 */
	@Override
	public Lock readLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.readLock();
	}

	/**
	 * 设置读写锁
	 * 
	 * @param readWriteLock 读写锁实例
	 */
	public void setReadWriteLock(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	/**
	 * 返回对象的字符串表示
	 * 
	 * @return 对象的字符串表示，如果容器为空则返回null
	 */
	@Override
	public String toString() {
		return read((e) -> e == null ? null : e.toString());
	}

	/**
	 * 执行，但不会从供应商获取container
	 * 
	 * @param <R>      返回值类型
	 * @param executor 执行函数，回调参数可能为空
	 * @return 执行结果
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
	 * @param <R>    返回值类型
	 * @param writer 写入函数，回调参数不会为空
	 * @return 写入结果
	 * @throws X 可能抛出的异常
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

	/**
	 * 获取写锁
	 * 
	 * @return 写锁实例
	 */
	@Override
	public Lock writeLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.writeLock();
	}
}