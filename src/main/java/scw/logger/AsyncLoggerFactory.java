package scw.logger;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.UnsafeStringBuffer;

public class AsyncLoggerFactory extends AbstractMyLoggerFactory implements Runnable {
	private final LinkedBlockingQueue<Message> handlerQueue;
	private final Thread thread;
	private final UnsafeStringBuffer unsafeStringBuffer;

	public AsyncLoggerFactory(String threadName) {
		handlerQueue = new LinkedBlockingQueue<Message>();
		unsafeStringBuffer = new UnsafeStringBuffer();
		thread = new Thread(this, threadName);
		thread.start();
	}

	public void log(Message message) {
		handlerQueue.offer(message);
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				Message message = handlerQueue.take();
				if (message == null) {
					continue;
				}

				try {
					console(unsafeStringBuffer, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	protected String getMessage(Message message) throws Exception {
		message.appendTo(unsafeStringBuffer);
		return unsafeStringBuffer.toString();
	}

	public void destroy() {
		thread.interrupt();
	}
}
