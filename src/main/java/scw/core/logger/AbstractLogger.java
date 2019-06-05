package scw.core.logger;

public abstract class AbstractLogger implements Logger {
	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final boolean warnEnabled;
	private final boolean errorEnabled;
	private final String tag;
	private final String placeholder;

	public AbstractLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, boolean warnEnabled,
			boolean errorEnabled, String tag, String placeholder) {
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
		this.tag = tag;
		this.placeholder = placeholder;
	}

	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	public void trace(String format, Object... args) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, tag, format, args, null, placeholder));
	}

	public void trace(Throwable t, String msg, Object... args) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, tag, msg, args, t, placeholder));
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void debug(String format, Object... args) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, tag, format, args, null, placeholder));
	}

	public void debug(Throwable t, String msg, Object... args) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, tag, msg, args, t, placeholder));
	}

	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	public void info(String format, Object... args) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, tag, format, args, null, placeholder));
	}

	public void info(Throwable e, String msg, Object... args) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, tag, msg, null, e, placeholder));
	}

	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	public void warn(String format, Object... args) {
		if (!warnEnabled) {
			return;
		}

		log(new Message(Level.WARN, tag, format, args, null, placeholder));
	}

	public void warn(Throwable e, String msg, Object... args) {
		if (!warnEnabled) {
			return;
		}

		log(new Message(Level.WARN, tag, msg, args, e, placeholder));
	}

	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	public void error(String format, Object... args) {
		if (!errorEnabled) {
			return;
		}

		log(new Message(Level.ERROR, tag, format, args, null, placeholder));
	}

	public void error(Throwable e, String msg, Object... args) {
		if (!errorEnabled) {
			return;
		}

		log(new Message(Level.ERROR, tag, msg, args, e, placeholder));
	}

	protected abstract void log(Message message);
}
