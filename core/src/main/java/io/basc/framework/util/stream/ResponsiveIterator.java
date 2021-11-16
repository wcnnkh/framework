package io.basc.framework.util.stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StaticSupplier;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 响应式迭代
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public class ResponsiveIterator<E> implements Iterator<E>, AutoCloseable {
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final BlockingQueue<ResponsiveMessage<E>> queue;
	private volatile Supplier<E> valueSupplier;

	/**
	 * 默认缓存16条消息
	 */
	public ResponsiveIterator() {
		this(16);
	}

	/**
	 * @param capacity
	 *            缓存的消息数量
	 */
	public ResponsiveIterator(int capacity) {
		this.queue = new ArrayBlockingQueue<>(capacity);
	}

	@Override
	public void close() throws InterruptedException {
		if (closed.compareAndSet(false, true)) {
			queue.put(new ResponsiveMessage<E>(1, null));
		}
	}

	/**
	 * 推送一条消息,当缓存消息已满时将等待
	 * @see BlockingQueue#put(Object)
	 * @param message
	 * @throws InterruptedException
	 */
	public void put(E message) throws InterruptedException {
		queue.put(new ResponsiveMessage<>(0, message));
	}

	/**
	 * @see BlockingQueue#offer(Object)
	 * @param message
	 * @return
	 */
	public boolean offer(E message) {
		return queue.offer(new ResponsiveMessage<>(0, message));
	}

	/**
	 * @see BlockingQueue#offer(Object, long, TimeUnit)
	 * @param message
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public boolean offer(E message, long timeout, TimeUnit unit)
			throws InterruptedException {
		return queue.offer(new ResponsiveMessage<>(0, message), timeout, unit);
	}

	/**
	 * 是否存在下一个值，如果不存在将等待，直到关闭{@link #close()}
	 * @return
	 */
	@Override
	public boolean hasNext() {
		if (valueSupplier == null) {
			synchronized (this) {
				if (valueSupplier == null) {
					if (closed.get() && queue.isEmpty()) {
						// 已经关闭且没消息了
						return false;
					}

					try {
						ResponsiveMessage<E> message = queue.take();
						if (message.type == 1) {
							// 关闭消息
							return false;
						}
						this.valueSupplier = new StaticSupplier<E>(
								message.value);
						return true;
					} catch (InterruptedException e) {
						// 线程中断返回false
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 是否存在下一个值，如果不存在将等待指定时间
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public boolean hasNext(long timeout, TimeUnit unit) {
		if (valueSupplier == null) {
			synchronized (this) {
				if (valueSupplier == null) {
					if (closed.get() && queue.isEmpty()) {
						// 已经关闭且没消息了
						return false;
					}

					try {
						ResponsiveMessage<E> message = queue
								.poll(timeout, unit);
						if (message == null) {
							// 超时了，返回false
							return false;
						}

						if (message.type == 1) {
							// 关闭消息
							return false;
						}
						this.valueSupplier = new StaticSupplier<E>(
								message.value);
						return true;
					} catch (InterruptedException e) {
						// 线程中断返回false
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		synchronized (this) {
			E value = valueSupplier.get();
			valueSupplier = null;
			return value;
		}
	}

	private static class ResponsiveMessage<V> {
		public final V value;
		/**
		 * 消息类型 0是普通消息， 1是关闭消息
		 */
		@Nullable
		public final int type;

		public ResponsiveMessage(int type, @Nullable V value) {
			this.value = value;
			this.type = type;
		}
	}
}