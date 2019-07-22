package scw.logger;

import scw.core.utils.LoggerUtils;
import scw.core.utils.StringAppend;

public final class Message implements StringAppend {
	private final long cts;
	private final Level level;
	private final StringAppend msg;
	private final String tag;
	private final Throwable throwable;

	public Message(Level level, String tag, StringAppend msg, Throwable throwable) {
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

	public StringAppend getMsg() {
		return msg;
	}

	public String getTag() {
		return tag;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void appendTo(Appendable appendable) throws Exception {
		LoggerUtils.loggerAppend(appendable, cts, level.name(), tag, msg);
	}
}