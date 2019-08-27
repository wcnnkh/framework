package scw.logger;

public abstract class AbstractMyLogger extends AbstractLogger implements Logger {
	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final String name;

	public AbstractMyLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, String name,
			String placeholder) {
		super(placeholder);
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.name = name;
	}

	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	public void trace(Object format, Object... args) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, name, format, args, null, getPlaceholder()));
	}

	public void trace(Throwable t, Object msg, Object... args) {
		if (!traceEnabled) {
			return;
		}

		log(new Message(Level.TRACE, name, msg, args, t, getPlaceholder()));
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void debug(Object format, Object... args) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, name, format, args, null, getPlaceholder()));
	}

	public void debug(Throwable t, Object msg, Object... args) {
		if (!debugEnabled) {
			return;
		}

		log(new Message(Level.DEBUG, name, msg, args, t, getPlaceholder()));
	}

	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	public void info(Object format, Object... args) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, name, format, args, null, getPlaceholder()));
	}

	public void info(Throwable e, Object msg, Object... args) {
		if (!infoEnabled) {
			return;
		}

		log(new Message(Level.INFO, name, msg, null, e, getPlaceholder()));
	}

	public void warn(Object format, Object... args) {
		if (!isWarnEnabled()) {
			return;
		}

		log(new Message(Level.WARN, name, format, args, null, getPlaceholder()));
	}

	public void warn(Throwable e, Object msg, Object... args) {
		if (!isWarnEnabled()) {
			return;
		}

		log(new Message(Level.WARN, name, msg, args, e, getPlaceholder()));
	}

	public void error(Object format, Object... args) {
		if (!isErrorEnabled()) {
			return;
		}

		log(new Message(Level.ERROR, name, format, args, null, getPlaceholder()));
	}

	public void error(Throwable e, Object msg, Object... args) {
		if (!isErrorEnabled()) {
			return;
		}

		log(new Message(Level.ERROR, name, msg, args, e, getPlaceholder()));
	}

	public String getName() {
		return name;
	}

	protected abstract void log(Message message);
}
