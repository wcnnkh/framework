package scw.logger;

public abstract class AbstractMessageLogger extends AbstractLogger implements Logger {
	private final String name;

	public AbstractMessageLogger(Level level, String name, String placeholder) {
		super(level, placeholder);
		this.name = name;
	}

	public void trace(Object format, Object... args) {
		if (!isTraceEnabled()) {
			return;
		}

		log(new Message(Level.TRACE, name, format, args, null, getPlaceholder()));
	}

	public void trace(Throwable t, Object msg, Object... args) {
		if (!isTraceEnabled()) {
			return;
		}

		log(new Message(Level.TRACE, name, msg, args, t, getPlaceholder()));
	}

	public void debug(Object format, Object... args) {
		if (!isDebugEnabled()) {
			return;
		}

		log(new Message(Level.DEBUG, name, format, args, null, getPlaceholder()));
	}

	public void debug(Throwable t, Object msg, Object... args) {
		if (!isDebugEnabled()) {
			return;
		}

		log(new Message(Level.DEBUG, name, msg, args, t, getPlaceholder()));
	}

	public void info(Object format, Object... args) {
		if (!isInfoEnabled()) {
			return;
		}

		log(new Message(Level.INFO, name, format, args, null, getPlaceholder()));
	}

	public void info(Throwable e, Object msg, Object... args) {
		if (!isInfoEnabled()) {
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
