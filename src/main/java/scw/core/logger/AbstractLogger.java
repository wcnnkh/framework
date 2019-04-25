package scw.core.logger;

public abstract class AbstractLogger implements Logger {
	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final boolean warnEnabled;
	private final boolean errorEnabled;
	private final String tag;

	public AbstractLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, boolean warnEnabled,
			boolean errorEnabled, String tag) {
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
		this.tag = tag;
	}

	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	public void trace(String msg) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, tag, msg, null, null, null));
	}

	public void trace(String format, Object... args) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, tag, format, args, null, null));
	}

	public void trace(String msg, Throwable t) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, tag, msg, null, t, null));
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void debug(String msg) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, tag, msg, null, null, null));
	}

	public void debug(String format, Object... args) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, tag, format, args, null, null));
	}

	public void debug(String msg, Throwable t) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, tag, msg, null, t, null));

	}

	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	public void info(String msg) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, tag, msg, null, null, null));
	}

	public void info(String format, Object... args) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, tag, format, args, null, null));
	}

	public void info(String msg, Throwable t) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, tag, msg, null, t, null));
	}

	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	public void warn(String msg) {
		if (!warnEnabled) {
			return;
		}

		log(new Message(Level.WARN, tag, msg, null, null, null));
	}

	public void warn(String format, Object... args) {
		if (!warnEnabled) {
			return;
		}

		log(new Message(Level.WARN, tag, format, args, null, null));
	}

	public void warn(String msg, Throwable t) {
		if (!warnEnabled) {
			return;
		}

		log(new Message(Level.WARN, tag, msg, null, t, null));
	}

	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	public void error(String msg) {
		if (!errorEnabled) {
			return;
		}

		log(new Message(Level.ERROR, tag, msg, null, null, null));
	}

	public void error(String format, Object... args) {
		if (!errorEnabled) {
			return;
		}

		log(new Message(Level.ERROR, tag, format, args, null, null));
	}

	public void error(String msg, Throwable t) {
		if (!errorEnabled) {
			return;
		}

		log(new Message(Level.ERROR, tag, msg, null, t, null));
	}

	protected abstract void log(Message message);
}
