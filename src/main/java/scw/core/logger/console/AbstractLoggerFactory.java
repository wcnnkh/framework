package scw.core.logger.console;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.logger.ILoggerFactory;
import scw.core.logger.LoggerUtils;
import scw.core.logger.Message;

public abstract class AbstractLoggerFactory implements ILoggerFactory, Runnable {
	private LinkedBlockingQueue<Message> handlerQueue = new LinkedBlockingQueue<Message>();
	private final Thread thread;

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
				out(message);
				message = null;
			}
		} catch (InterruptedException e) {
		}
	}

	protected abstract void out(Message message);

	public void console(Message message) {
		String msg = LoggerUtils.getLogMessage(message.getCts(), message.getLevel().name(), message.getTag(),
				message.getPlaceholder(), message.getMsg(), message.getParams());
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
