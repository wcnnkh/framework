package scw.logger;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.UnsafeStringBuffer;

public abstract class AsyncLoggerFactory implements ILoggerFactory, Runnable {
	private LinkedBlockingQueue<Message> handlerQueue = new LinkedBlockingQueue<Message>();
	private final Thread thread;
	private final UnsafeStringBuffer unsafeStringBuffer = new UnsafeStringBuffer();

	public AsyncLoggerFactory(String threadName) {
		thread = new Thread(this, threadName);
		thread.start();
	}

	public void log(Message message) {
		handlerQueue.offer(message);
	}

	public Logger getLogger(String name) {
		return new AsyncLogger(true, true, true, true, true, name, this);
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				Message message = handlerQueue.take();
				if (message == null) {
					continue;
				}

				unsafeStringBuffer.reset();
				try {
					message.appendTo(unsafeStringBuffer);
					String msg = unsafeStringBuffer.toString();
					out(message.getTag(), message.getLevel(), msg, message.getThrowable());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	public void out(String name, Level level, String msg, Throwable e) throws Exception {
		switch (level) {
		case ERROR:
		case WARN:
			System.err.println(msg);
			break;
		default:
			System.out.println(msg);
			break;
		}

		if (e != null) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		thread.interrupt();
	}
}
