package scw.logger;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.UnsafeStringBuffer;

public abstract class AsyncLoggerFactory implements ILoggerFactory, Runnable {
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

	public Logger getLogger(String name) {
		return new AsyncLogger(true, true, true, name, this);
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				Message message = handlerQueue.take();
				if (message == null) {
					continue;
				}

				try {
					out(unsafeStringBuffer, message);
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

	public void out(UnsafeStringBuffer unsafeStringBuffer, Message message) throws Exception {
		String msg = message.toString(unsafeStringBuffer);
		switch (message.getLevel()) {
		case ERROR:
		case WARN:
			System.err.println(msg);
			break;
		default:
			System.out.println(msg);
			break;
		}

		if (message.getThrowable() != null) {
			message.getThrowable().printStackTrace();
		}
	}

	public void destroy() {
		thread.interrupt();
	}
}
