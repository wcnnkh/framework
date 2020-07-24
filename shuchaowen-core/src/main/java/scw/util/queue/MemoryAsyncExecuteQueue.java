package scw.util.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import scw.beans.Destroy;
import scw.core.utils.StringUtils;
import scw.util.Consumer;

public final class MemoryAsyncExecuteQueue<E> implements AsyncExecuteQueue<E>,
		Runnable, Destroy, Consumer<E> {
	private List<Consumer<E>> consumers = new ArrayList<Consumer<E>>();
	private BlockingQueue<E> blockingQueue;
	private Thread thread;
	private volatile boolean started = true;

	public MemoryAsyncExecuteQueue(String threadName, Boolean daemon) {
		this(new LinkedBlockingQueue<E>(), threadName, daemon);
	}

	public MemoryAsyncExecuteQueue(BlockingQueue<E> blockingQueue,
			String threadName, Boolean daemon) {
		this.blockingQueue = blockingQueue;
		thread = new Thread(this, StringUtils.isEmpty(threadName) ? getClass()
				.getName() : threadName);
		if (daemon != null) {
			thread.setDaemon(daemon);
		}
		thread.start();

		Thread shutdown = new Thread() {
			@Override
			public void run() {
				MemoryAsyncExecuteQueue.this.destroy();
			}
		};
		shutdown.setName(thread.getName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
	}

	public void run() {
		while (!thread.isInterrupted()) {
			synchronized (this) {
				E message;
				try {
					message = blockingQueue.take();
				} catch (InterruptedException e) {
					break;
				}

				if (message == null) {
					continue;
				}

				accept(message);
			}
		}
	}

	public void accept(E message) {
		for (Consumer<E> consumer : consumers) {
			consumer.accept(message);
		}
	}

	public synchronized void destroy() {
		if (!started) {
			return;
		}

		if (!thread.isInterrupted()) {
			thread.interrupt();
		}

		synchronized (this) {
			while (!blockingQueue.isEmpty()) {
				E message = blockingQueue.poll();
				if (message == null) {
					continue;
				}

				accept(message);
			}
		}
		started = false;
	}

	public void put(E message) {
		if (!started) {
			accept(message);
			return;
		}

		if (!blockingQueue.offer(message)) {
			try {
				blockingQueue.put(message);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void addConsumer(Consumer<E> consumer) {
		consumers.add(consumer);
	}

}
