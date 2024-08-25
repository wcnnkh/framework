package io.basc.framework.util.register.container;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import io.basc.framework.util.ObjectUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个懒惰的容器定义
 * 
 * @author shuchaowen
 *
 * @param <C>
 */
@RequiredArgsConstructor
public class LazyContainer<C> {
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private volatile C container;
	@NonNull
	private final Supplier<? extends C> containerSupplier;

	/**
	 * 测试container
	 * 
	 * @param predicate 回调参数可能为空
	 * @return
	 */
	public boolean test(Predicate<? super C> predicate) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return predicate.test(container);
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
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return reader.applyAsInt(container);
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
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return reader.apply(container);
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
		Lock lock = readWriteLock.writeLock();
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

	protected C newContainer() {
		return containerSupplier.get();
	}

	/**
	 * 执行，但不会从供应商获取container
	 * 
	 * @param executor 回调参数不会为空
	 * @return members为空会直接返回false, 或executor的返回值
	 */
	public boolean execute(Predicate<? super C> executor) {
		Lock lock = readWriteLock.writeLock();
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
	 * 执行，但不会从供应商获取container
	 * 
	 * @param <R>
	 * @param executor 回调参数可能为空
	 * @return
	 */
	public <R> R update(Function<? super C, R> executor) {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			return executor.apply(container);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof LazyContainer) {
			LazyContainer<?> other = (LazyContainer<?>) obj;
			return test((o1) -> other.test((o2) -> ObjectUtils.equals(o1, o2)));
		}

		return test((conainer) -> ObjectUtils.equals(conainer, obj));
	}

	@Override
	public int hashCode() {
		return readInt((conainer) -> ObjectUtils.hashCode(conainer));
	}

	@Override
	public String toString() {
		return read((e) -> e == null ? null : e.toString());
	}
}
