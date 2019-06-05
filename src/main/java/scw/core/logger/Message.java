package scw.core.logger;

import java.io.IOException;

public final class Message implements LoggerAppend {
	private final long cts;
	private final Level level;
	private final LoggerAppend msg;
	private final String tag;
	private final Throwable throwable;

	public Message(Level level, String tag, LoggerAppend msg, Throwable throwable) {
		this.cts = System.currentTimeMillis();
		this.level = level;
		this.msg = msg;
		this.throwable = throwable;
		this.tag = tag;
	}

	public Message(Level level, String tag, String msg, Object[] args, Throwable throwable, String placeholder) {
		this.cts = System.currentTimeMillis();
		this.level = level;
		this.throwable = throwable;
		this.tag = tag;
		this.msg = new DefaultLoggerFormatAppend(msg, placeholder, args);
	}

	public long getCts() {
		return cts;
	}

	public Level getLevel() {
		return level;
	}

	public LoggerAppend getMsg() {
		return msg;
	}

	public String getTag() {
		return tag;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void appendLogger(Appendable appendable) throws IOException {
		LoggerUtils.loggerAppend(appendable, cts, level.name(), tag, msg);
	}
}