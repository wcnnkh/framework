package run.soeasy.framework.core.concurrent;

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
import run.soeasy.framework.core.concurrent.locks.NoOpLock;
import run.soeasy.framework.core.function.ThrowingSupplier;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 使用复制读来实现线程安全的容器包装类
 * <p>
 * 该类通过读写锁机制保护内部容器，实现线程安全的读写操作。 读取操作返回容器内容的副本，确保读取操作的线程安全性。 写入操作使用写锁保证互斥性。
 * <p>
 * 容器采用延迟初始化策略，首次写入时通过{@link ThrowingSupplier}创建容器实例。
 *
 * @param <C> 容器类型
 * @param <X> 容器初始化可能抛出的异常类型
 * 
 * @author soeasy.run
 */
public class LockableContainer<C, X extends Throwable> implements ReadWriteLock {
	/**
	 * 内部容器实例，使用volatile保证可见性
	 */
	private volatile C container;

	/**
	 * 容器数据源，用于延迟初始化容器
	 */
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
	 * <p>
	 * 此方法会比较当前容器内容与另一个对象的相等性。 如果另一个对象是LockableContainer，则比较其内部容器内容。
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
	 * <p>
	 * 如果容器尚未初始化（即container为null），则直接返回false。 否则执行给定的断言函数并返回结果。
	 * 
	 * @param executor 回调参数不会为空
	 * @return members为空会直接返回false, 或executor的返回值
	 */
	public boolean execute(@NonNull Predicate<? super C> executor) {
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
	 * <p>
	 * 此方法返回内部容器内容的哈希值。 如果容器为null，则返回0。
	 * 
	 * @return 对象的哈希值
	 */
	@Override
	public int hashCode() {
		return readAsInt((conainer) -> ObjectUtils.hashCode(conainer));
	}

	/**
	 * 是否是线程安全的
	 * <p>
	 * 如果设置了实际的读写锁（非NoOpLock），则认为是线程安全的。
	 * 
	 * @return 如果是线程安全的返回true，否则返回false
	 */
	public boolean isThreadSafe() {
		return readWriteLock != null;
	}

	/**
	 * 初始化容器时的回调
	 * <p>
	 * 子类可以重写此方法自定义容器初始化逻辑。 默认实现调用containerSource获取容器实例。
	 * 
	 * @return 新的容器实例
	 * @throws X 可能抛出的异常
	 */
	protected C newContainer() throws X {
		return containerSource.get();
	}

	/**
	 * 重置容器，将容器置为null
	 * <p>
	 * 下次访问时将重新从供应商获取容器实例。
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
	 * <p>
	 * 此方法使用读锁保护，确保读取操作的线程安全性。 读取操作返回当前容器内容的引用，可能为null。
	 * 
	 * @param <R>    返回值类型
	 * @param reader 读取容器内容的函数，回调参数可能为空
	 * @return 读取结果
	 */
	public <R> R read(@NonNull Function<? super C, ? extends R> reader) {
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
	 * <p>
	 * 此方法使用读锁保护，确保测试操作的线程安全性。
	 * 
	 * @param predicate 测试容器内容的断言函数，回调参数可能为空
	 * @return 测试结果
	 */
	public boolean readAsBoolean(@NonNull Predicate<? super C> predicate) {
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
	 * <p>
	 * 此方法使用读锁保护，并确保返回的列表是线程安全的。 如果容器内容本身是线程安全的列表，则直接返回； 否则创建一个新的列表副本。
	 * 
	 * @param <E>    列表元素类型
	 * @param reader 读取容器内容的函数
	 * @return 列表形式的容器内容
	 */
	public <E> List<E> readAsList(@NonNull Function<? super C, ? extends List<E>> reader) {
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
	 * <p>
	 * 如果列表为空或已经是线程安全的类型，则直接返回原列表； 否则创建一个新的列表副本。
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
	 * <p>
	 * 如果映射为空或已经是线程安全的类型，则直接返回原映射； 否则创建一个新的映射副本。
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param map 原始映射
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
	 * <p>
	 * 如果集合为空或已经是线程安全的类型，则直接返回原集合； 否则创建一个新的集合副本。
	 * 
	 * @param <T> 集合元素类型
	 * @param set 原始集合
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
	 * <p>
	 * 此方法使用写锁保护，确保写入操作的线程安全性。 如果容器尚未初始化，则先从供应商获取容器实例。
	 * 
	 * @param <E>    列表元素类型
	 * @param writer 写入容器内容的函数
	 * @return 列表形式的容器内容
	 * @throws X 可能抛出的异常
	 */
	public <E> List<E> writeAsList(@NonNull Function<? super C, ? extends List<E>> writer) throws X {
		return write((c) -> {
			List<E> elements = writer.apply(c);
			return shared(elements);
		});
	}

	/**
	 * 更新并返回列表形式的容器内容
	 * <p>
	 * 此方法使用写锁保护，确保更新操作的线程安全性。 与writeAsList不同，此方法不会初始化容器， 如果容器为null，则传递给函数的参数为null。
	 * 
	 * @param <E>      列表元素类型
	 * @param executor 更新容器内容的函数
	 * @return 列表形式的容器内容
	 */
	public <E> List<E> updateAsList(@NonNull Function<? super C, ? extends List<E>> executor) {
		return update((c) -> {
			List<E> elements = executor.apply(c);
			return shared(elements);
		});
	}

	/**
	 * 读取容器内容并转换为映射
	 * <p>
	 * 此方法使用读锁保护，并确保返回的映射是线程安全的。
	 * 
	 * @param <K>    键类型
	 * @param <V>    值类型
	 * @param reader 读取容器内容的函数
	 * @return 映射形式的容器内容
	 */
	public <K, V> Map<K, V> readAsMap(@NonNull Function<? super C, ? extends Map<K, V>> reader) {
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
	 * <p>
	 * 此方法使用读锁保护，并确保返回的集合是线程安全的。
	 * 
	 * @param <E>    集合元素类型
	 * @param reader 读取容器内容的函数
	 * @return 集合形式的容器内容
	 */
	public <E> Set<E> readAsSet(@NonNull Function<? super C, ? extends Set<E>> reader) {
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
	 * <p>
	 * Elements是框架自定义的集合接口，此方法确保返回的Elements是线程安全的。
	 * 
	 * @param <T>      元素类型
	 * @param elements 原始Elements
	 * @return 共享副本Elements
	 */
	protected <T> Streamable<T> shared(Streamable<T> elements) {
		if (elements == null) {
			return null;
		}

		if (isThreadSafe()) {
			return elements.cached();
		}
		return elements;
	}

	/**
	 * 容器可能为空
	 * <p>
	 * 此方法使用读锁保护，并确保返回的Elements是线程安全的。
	 * 
	 * @param <E>    元素类型
	 * @param reader 读取容器内容的函数
	 * @return Elements形式的容器内容
	 */
	public <E> Streamable<E> readAsElements(@NonNull Function<? super C, ? extends Streamable<E>> reader) {
		Lock lock = readLock();
		lock.lock();
		try {
			Streamable<E> elements = reader.apply(container);
			return shared(elements);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 读取并返回一个int
	 * <p>
	 * 此方法使用读锁保护，确保读取操作的线程安全性。
	 * 
	 * @param reader 读取容器内容的函数，回调参数可能为空
	 * @return 整数值
	 */
	public int readAsInt(@NonNull ToIntFunction<? super C> reader) {
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
	 * <p>
	 * 如果未设置读写锁，则返回NoOpLock.NO（无操作锁）。
	 * 
	 * @return 读锁实例
	 */
	@Override
	public Lock readLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.readLock();
	}

	/**
	 * 设置读写锁
	 * <p>
	 * 可在运行时动态设置读写锁实现。 设置为null将禁用线程安全功能。
	 * 
	 * @param readWriteLock 读写锁实例
	 */
	public void setReadWriteLock(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	/**
	 * 返回对象的字符串表示
	 * <p>
	 * 此方法返回内部容器内容的字符串表示。 如果容器为null，则返回"null"。
	 * 
	 * @return 对象的字符串表示，如果容器为空则返回null
	 */
	@Override
	public String toString() {
		return read((e) -> e == null ? null : e.toString());
	}

	/**
	 * 执行，但不会从供应商获取container
	 * <p>
	 * 此方法使用写锁保护，确保更新操作的线程安全性。 与write不同，此方法不会初始化容器。
	 * 
	 * @param <R>      返回值类型
	 * @param executor 执行函数，函数参数可能为空
	 * @return 执行结果
	 */
	public <R> R update(@NonNull Function<? super C, R> executor) {
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
	 * <p>
	 * 此方法使用写锁保护，确保写入操作的线程安全性。 如果容器尚未初始化，则先从供应商获取容器实例。
	 * 
	 * @param <R>    返回值类型
	 * @param writer 写入函数，回调参数不会为空
	 * @return 写入结果
	 * @throws X 可能抛出的异常
	 */
	public <R> R write(@NonNull Function<? super C, R> writer) throws X {
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
	 * <p>
	 * 如果未设置读写锁，则返回NoOpLock.NO（无操作锁）。
	 * 
	 * @return 写锁实例
	 */
	@Override
	public Lock writeLock() {
		return readWriteLock == null ? NoOpLock.NO : readWriteLock.writeLock();
	}
}