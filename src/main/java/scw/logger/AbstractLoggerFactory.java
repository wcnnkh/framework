package scw.logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;

public abstract class AbstractLoggerFactory implements ILoggerFactory, Runnable {
	private Queue<Message> handlerQueue = new ConcurrentLinkedQueue<Message>();
	private final Thread thread;
	private AtomicLong index = new AtomicLong();

	public AbstractLoggerFactory() {
		System.out.println("Init shuchaowen-logger [" + this.getClass().getName() + "]");
		thread = new Thread(this, "shuchaowen-logger");
		thread.start();
	}

	public void log(Message message) {
		handlerQueue.offer(message);
		index.incrementAndGet();
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				if (index.get() == 0) {
					Thread.sleep(1);
					continue;
				}

				Message message = handlerQueue.poll();
				index.decrementAndGet();
				out(message);
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
	}

	public void destroy() {
		thread.interrupt();
	}
}
