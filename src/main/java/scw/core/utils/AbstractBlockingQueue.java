package scw.core.utils;

import scw.core.Consumer;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.Producer;

public abstract class AbstractBlockingQueue<T> implements Consumer<T>, Producer<T>, Init, Destroy, Runnable {
	private BlockingQueue<T> blockingQueue;
	private Thread thread = new Thread(this);

	protected BlockingQueue<T> getBlockingQueue() {
		return blockingQueue;
	}

	public AbstractBlockingQueue(BlockingQueue<T> queue) {
		this.blockingQueue = queue;
	}

	public void init() {
		thread.start();
	}

	public void destroy() {
		thread.interrupt();
	}

	public abstract void consume(T message);

	public void run() {
		try {
			while (!Thread.interrupted()) {
				T message = blockingQueue.take();
				if (message == null) {
					continue;
				}

				try {
					consume(message);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	public void push(T message) {
		try {
			blockingQueue.put(message);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
