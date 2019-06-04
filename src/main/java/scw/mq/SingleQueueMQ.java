package scw.mq;

import scw.core.Consumer;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.Producer;
import scw.data.utils.Queue;

public abstract class SingleQueueMQ<T> implements Consumer<T>, Producer<T>, Init, Destroy, Runnable {
	private Queue<T> queue;
	private Thread thread = new Thread(this);

	protected Queue<T> getQueue() {
		return queue;
	}

	public SingleQueueMQ(Queue<T> queue) {
		this.queue = queue;
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
				T message = queue.take();
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
		queue.offer(message);
	}

}
