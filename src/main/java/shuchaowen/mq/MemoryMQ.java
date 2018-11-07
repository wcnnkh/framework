package shuchaowen.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public final class MemoryMQ<T> implements MQ<T> {
	private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	private final Thread thread;

	private List<Consumer<T>> consumerList;

	public MemoryMQ() {
		this.thread = new Thread(new Runnable() {

			public void run() {
				boolean find = false;
				T message;
				try {
					while (!Thread.interrupted()) {
						if (consumerList == null || consumerList.isEmpty()) {
							continue;
						}

						message = queue.peek();
						if (message == null) {
							continue;
						}

						try {
							find = true;
							for (Consumer<T> consumer : consumerList) {
								consumer.handler(message);
							}
							queue.poll();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (!find) {
							Thread.sleep(100L);
						}
						find = false;
					}
				} catch (InterruptedException e) {
				}
			}
		});
	}

	public void push(T message) {
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public synchronized void consumer(Consumer<T> consumer) {
		if (consumerList == null) {
			consumerList = new ArrayList<Consumer<T>>();
		}
		consumerList.add(consumer);
	}

	public void destroy() {
		thread.interrupt();
	}

	public void start() {
		this.thread.start();
	}
}
