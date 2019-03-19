package scw.logger.console;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.logger.ILoggerFactory;
import scw.logger.Logger;
import scw.logger.Message;

public class ConsoleLoggerFactory implements ILoggerFactory, Runnable {
	private Queue<Message> handlerQueue = new ConcurrentLinkedQueue<Message>();
	private final Thread thread;

	public ConsoleLoggerFactory() {
		System.out.println("Init shuchaowen-logger [" + this.getClass().getName() + "]");
		thread = new Thread(this, "shuchaowen-logger");
		thread.start();
	}

	public Logger getLogger(String name) {
		return new ConsoleLogger(true, true, true, true, true, name, this);
	}

	public void log(Message message) {
		handlerQueue.offer(message);
	}

	public void run() {
		while (!thread.isInterrupted()) {
			Message message = handlerQueue.poll();
			if (message == null) {
				continue;
			}

			console(message);
		}
	}

	protected void console(Message message) {
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
	}

	public void destroy() {
		thread.interrupt();
	}
}
