package scw.event.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import scw.event.Event;
import scw.lang.Nullable;

public class DefaultQueueEventDispatcher<T extends Event> extends DefaultEventDispatcher<T>
		implements Runnable {
	private BlockingQueue<T> blockingQueue;
	private Thread thread;
	private volatile boolean started = true;
	private volatile boolean destroy = false;// 是否销毁结束
	
	public DefaultQueueEventDispatcher() {
		this(null);
	}
	
	public DefaultQueueEventDispatcher(@Nullable String name) {
		this(new LinkedBlockingQueue<T>(), name);
	}

	public DefaultQueueEventDispatcher(BlockingQueue<T> blockingQueue, @Nullable String name) {
		super(true);
		this.blockingQueue = blockingQueue;
		thread = new Thread(this, name == null? getClass().getName():name);
		thread.setDaemon(true);
		thread.start();

		Thread shutdown = new Thread() {
			@Override
			public void run() {
				DefaultQueueEventDispatcher.this.destroy();
			}
		};
		shutdown.setName(thread.getName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
	}

	public void run() {
		while (!thread.isInterrupted() && started) {
			synchronized (blockingQueue) {
				T message;
				try {
					message = blockingQueue.take();
				} catch (InterruptedException e) {
					break;
				}

				if (message == null) {
					continue;
				}

				super.publishEvent(message);
			}
		}
	}

	public synchronized void destroy() {
		if (!started) {
			return;
		}

		if (!thread.isInterrupted()) {
			thread.interrupt();
		}

		started = false;
		synchronized (blockingQueue) {
			while (!blockingQueue.isEmpty()) {
				T message = blockingQueue.poll();
				if (message == null) {
					continue;
				}

				super.publishEvent(message);
			}
		}
		destroy = true;
	}

	@Override
	public void publishEvent(T event) {
		if (destroy) {// 如果已经销毁结束了
			super.publishEvent(event);
			return;
		}

		if (!blockingQueue.offer(event)) {
			try {
				blockingQueue.put(event);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isStarted() {
		return started;
	}
}
