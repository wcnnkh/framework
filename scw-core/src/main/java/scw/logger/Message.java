package scw.logger;

import java.io.IOException;
import java.util.logging.Level;

import scw.util.FormatUtils;
import scw.util.PlaceholderFormatAppend;
import scw.util.StringAppend;
import scw.util.Supplier;

public final class Message implements StringAppend, Supplier<String> {
	private final long cts;
	private final Level level;
	private final StringAppend msg;
	private final String name;
	private final Throwable throwable;

	public Message(Level level, String name, StringAppend msg, Throwable throwable) {
		this.cts = System.currentTimeMillis();
		this.level = level;
		this.msg = msg;
		this.throwable = throwable;
		this.name = name;
	}

	public Message(Level level, String name, Object msg, Object[] args, Throwable throwable, String placeholder) {
		this(level, name, new PlaceholderFormatAppend(msg, placeholder, args), throwable);
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

	public String getName() {
		return name;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void appendTo(Appendable appendable) throws IOException {
		FormatUtils.loggerAppend(appendable, cts, level.getName(), name, msg);
	}
	
	@Override
	public String get() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			appendTo(sb);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}
}