package scw.core.logger.console;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.logger.ILoggerFactory;
import scw.core.logger.Message;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;

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
		StringBuilder sb = new StringBuilder(512);
		sb.append(XTime.format(message.getCts(), "yyyy-MM-dd HH:mm:ss,SSS"));
		sb.append(" ").append(message.getLevel());
		if (message.getTag() != null) {
			sb.append(" [").append(message.getTag()).append("]");
		}

		sb.append(" - ");
		sb.append(StringUtils.format(message.getMsg(),
				StringUtils.isNull(message.getPlaceholder()) ? "{}" : message.getPlaceholder(), message.getParams()));

		switch (message.getLevel()) {
		case ERROR:
		case WARN:
			System.err.println(sb.toString());
			break;
		default:
			System.out.println(sb.toString());
			break;
		}

		if (message.getThrowable() != null) {
			message.getThrowable().printStackTrace();
		}
		sb = null;
	}

	public void destroy() {
		thread.interrupt();
	}
}
