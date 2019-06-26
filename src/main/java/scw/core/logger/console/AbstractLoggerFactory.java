package scw.core.logger.console;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.UnsafeStringBuffer;
import scw.core.logger.ILoggerFactory;
import scw.core.logger.Message;

public abstract class AbstractLoggerFactory implements ILoggerFactory, Runnable {
	private LinkedBlockingQueue<Message> handlerQueue = new LinkedBlockingQueue<Message>();
	private final Thread thread;
	private final UnsafeStringBuffer unsafeStringBuffer = new UnsafeStringBuffer();

	public AbstractLoggerFactory() {
		thread = new Thread(this, "shuchaowen-logger");
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

				unsafeStringBuffer.reset();
				try {
					out(unsafeStringBuffer, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				message = null;
			}
		} catch (InterruptedException e) {
		}
	}

	public void out(UnsafeStringBuffer append, Message message) throws Exception {
		message.appendTo(append);
		String msg = append.toString();
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
		msg = null;
	}

	public void destroy() {
		thread.interrupt();
	}
}
