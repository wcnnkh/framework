package scw.logger;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import scw.core.UnsafeStringBuffer;

public class AsyncConsoleLoggerFactory extends AbstractConsoleLoggerFactory implements Runnable {
	private final LinkedBlockingQueue<Message> handlerQueue;
	private final Thread thread;
	private final UnsafeStringBuffer unsafeStringBuffer;
	private volatile boolean shutdown = false;

	public AsyncConsoleLoggerFactory() {
		handlerQueue = new LinkedBlockingQueue<Message>();
		unsafeStringBuffer = new UnsafeStringBuffer();
		thread = new Thread(this, getClass().getSimpleName());
		thread.setDaemon(true);
		thread.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				AsyncConsoleLoggerFactory.this.destroy();
			}
		});
	}

	protected final void log(Message message) {
		handlerQueue.offer(message);
	}

	public void run() {
		while (!thread.isInterrupted()) {
			synchronized (handlerQueue) {
				if (shutdown && handlerQueue.isEmpty()) {
					break;
				}

				Message message;
				try {
					message = handlerQueue.take();
				} catch (InterruptedException e1) {
					break;
				}

				if (message == null) {
					continue;
				}

				try {
					console(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public synchronized void destroy() {
		if (shutdown) {
			return;
		}
		shutdown = true;
		thread.interrupt();
		synchronized (handlerQueue) {
			while(!handlerQueue.isEmpty()){
				Message message = handlerQueue.poll();
				if(message == null){
					continue;
				}
				
				try {
					console(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Appendable createAppendable() {
		unsafeStringBuffer.reset();
		return unsafeStringBuffer;
	}
}
