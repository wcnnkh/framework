package io.basc.framework.observe.container;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import io.basc.framework.observe.Observer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObservableContainer<E, C> extends Observer<E> {
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
				container = containerSupplier.get();
			}
			return writer.apply(container);
		} finally {
			lock.unlock();
		}
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

	public Supplier<? extends C> getContainerSupplier() {
		return containerSupplier;
	}
}
