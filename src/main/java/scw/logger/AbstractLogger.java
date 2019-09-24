package scw.logger;

public abstract class AbstractLogger implements Logger {
	private final String placeholder;
	private final Level level;

	public AbstractLogger(Level level, String placeholder) {
		this.level = level;
		this.placeholder = placeholder;
	}

	protected Object createMessage(Object format, Object... args) {
		return new DefaultLoggerFormatAppend(format, getPlaceholder(), args);
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void info(Object format) {
		info(format, EMPTY_ARGS);
	}

	public void trace(Object format) {
		trace(format, EMPTY_ARGS);
	}

	public boolean isWarnEnabled() {
		return Level.WARN.enabled(level);
	}
	
	public boolean isDebugEnabled() {
		return Level.DEBUG.enabled(level);
	}
	
	public boolean isInfoEnabled() {
		return Level.INFO.enabled(level);
	}
	
	public boolean isTraceEnabled() {
		return Level.TRACE.enabled(level);
	}

	public void warn(Object format) {
		warn(format, EMPTY_ARGS);
	}

	public boolean isErrorEnabled() {
		return Level.ERROR.enabled(level);
	}

	public void error(Object format) {
		error(format, EMPTY_ARGS);
	}

	public void debug(Object format) {
		debug(format, EMPTY_ARGS);
	}
}
