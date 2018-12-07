package shuchaowen.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;

public final class MemoryMQ<T> implements MQ<T> {
	private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	private final Thread thread;

	private List<Consumer<T>> consumerList;

	public MemoryMQ() {
		this.thread = new Thread(new Runnable() {

			public void run() {
				long sleepTime = 0L;
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
							for (Consumer<T> consumer : consumerList) {
								consumer.handler(message);
							}
							queue.poll();
							sleepTime = 0;
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						if(sleepTime > 0){
							Thread.sleep(sleepTime);
						}
						sleepTime = Math.min(5000L, sleepTime + 100);
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
