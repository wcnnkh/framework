package scw.logger;

public final class Message {
	private final long cts;
	private final Level level;
	private final String msg;
	private final Object[] params;
	private final String tag;
	private final Throwable throwable;
	private final String placeholder;

	public Message(Level level, String tag, String msg, Object[] params, Throwable throwable, String placeholder) {
		this.cts = System.currentTimeMillis();
		this.msg = msg;
		this.level = level;
		this.params = params;
		this.throwable = throwable;
		this.placeholder = placeholder;
		this.tag = tag;
	}

	public long getCts() {
		return cts;
	}

	public Level getLevel() {
		return level;
	}

	public String getMsg() {
		return msg;
	}

	public Object[] getParams() {
		return params;
	}

	public String getTag() {
		return tag;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public String getPlaceholder() {
		return placeholder;
	}
}
