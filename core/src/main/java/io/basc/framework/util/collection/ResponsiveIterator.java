package io.basc.framework.util.collection;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import io.basc.framework.util.function.Functions;

/**
 * 响应式迭代
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class ResponsiveIterator<E> implements CloseableIterator<E> {
	private static final int DEFAULT_CAPACITY = Integer
			.getInteger("io.basc.framework.util.stream.ResponsiveIterator.capacity", 128);
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final AtomicBoolean readClosed = new AtomicBoolean(false);
	private final BlockingQueue<ResponsiveMessage<E>> queue;
	private volatile Supplier<E> valueSupplier;

	/**
	 * 默认缓存16条消息
	 */
	public ResponsiveIterator() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * @param capacity 缓存的消息数量
	 */
	public ResponsiveIterator(int capacity) {
		this.queue = new ArrayBlockingQueue<>(capacity);
	}

	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void close() {
		if (closed.compareAndSet(false, true)) {
			try {
				queue.put(new ResponsiveMessage<E>(1, null));
			} catch (InterruptedException e) {
				// 线程中断，忽略
			}
		}
	}

	/**
	 * 推送一条消息,当缓存消息已满时将等待
	 * 
	 * @see BlockingQueue#put(Object)
	 * @param message
	 * @throws InterruptedException
	 */
	public void put(E message) throws InterruptedException {
		if (isClosed()) {
			return;
		}

		queue.put(new ResponsiveMessage<>(0, message));
	}

	/**
	 * @see BlockingQueue#offer(Object)
	 * @param message
	 * @return
	 */
	public boolean offer(E message) {
		if (isClosed()) {
			return false;
		}

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
	public boolean offer(E message, long timeout, TimeUnit unit) throws InterruptedException {
		if (isClosed()) {
			return false;
		}

		return queue.offer(new ResponsiveMessage<>(0, message), timeout, unit);
	}

	/**
	 * 是否存在下一个值，如果不存在将等待，直到关闭{@link #close()}
	 * 
	 * @return
	 */
	@Override
	public boolean hasNext() {
		if (readClosed.get()) {
			return false;
		}

		if (valueSupplier == null) {
			synchronized (this) {
				if (readClosed.get()) {
					return false;
				}

				if (valueSupplier == null) {
					if (isClosed() && queue.isEmpty()) {
						// 已经关闭且没消息了
						return false;
					}

					try {
						ResponsiveMessage<E> message = queue.take();
						if (message.type == 1) {
							// 关闭消息
							if (readClosed.compareAndSet(false, true)) {
								queue.clear();
							}
							return false;
						}
						this.valueSupplier = Functions.forValue(message.value);
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
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public boolean hasNext(long timeout, TimeUnit unit) {
		if (readClosed.get()) {
			return false;
		}

		if (valueSupplier == null) {
			synchronized (this) {
				if (readClosed.get()) {
					return false;
				}

				if (valueSupplier == null) {
					if (closed.get() && queue.isEmpty()) {
						// 已经关闭且没消息了
						return false;
					}

					try {
						ResponsiveMessage<E> message = queue.poll(timeout, unit);
						if (message == null) {
							// 超时了，返回false
							return false;
						}

						if (message.type == 1) {
							// 关闭消息
							if (readClosed.compareAndSet(false, true)) {
								queue.clear();
							}
							return false;
						}
						this.valueSupplier = Functions.forValue(message.value);
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
		public final int type;

		public ResponsiveMessage(int type, V value) {
			this.value = value;
			this.type = type;
		}
	}
}
