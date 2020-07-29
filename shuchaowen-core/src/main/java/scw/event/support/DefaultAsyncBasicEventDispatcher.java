package scw.event.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import scw.beans.Destroy;
import scw.core.utils.StringUtils;
import scw.event.Event;

public class DefaultAsyncBasicEventDispatcher<T extends Event> extends DefaultBasicEventDispatcher<T>
		implements Runnable, Destroy {
	private BlockingQueue<T> blockingQueue;
	private Thread thread;
	private volatile boolean started = true;
	private volatile boolean destroy = false;// 是否销毁结束

	public DefaultAsyncBasicEventDispatcher(boolean concurrent, String threadName, Boolean daemon) {
		this(concurrent, new LinkedBlockingQueue<T>(), threadName, daemon);
	}

	public DefaultAsyncBasicEventDispatcher(boolean concurrent, BlockingQueue<T> blockingQueue, String threadName,
			Boolean daemon) {
		super(concurrent);
		this.blockingQueue = blockingQueue;
		thread = new Thread(this, StringUtils.isEmpty(threadName) ? getClass().getName() : threadName);
		if (daemon != null) {
			thread.setDaemon(daemon);
		}
		thread.start();

		Thread shutdown = new Thread() {
			@Override
			public void run() {
				DefaultAsyncBasicEventDispatcher.this.destroy();
			}
		};
		shutdown.setName(thread.getName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
	}

	public void run() {
		while (!thread.isInterrupted() && started) {
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

	public synchronized void destroy() {
		if (!started) {
			return;
		}

		if (!thread.isInterrupted()) {
			thread.interrupt();
		}

		started = false;
		while (!blockingQueue.isEmpty()) {
			T message = blockingQueue.poll();
			if (message == null) {
				continue;
			}

			super.publishEvent(message);
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
