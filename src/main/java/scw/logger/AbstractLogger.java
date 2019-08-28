package scw.logger;

public abstract class AbstractLogger implements Logger {
	private final String placeholder;

	public AbstractLogger(String placeholder) {
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
		return true;
	}

	public void warn(Object format) {
		warn(format, EMPTY_ARGS);
	}

	public boolean isErrorEnabled() {
		return true;
	}

	public void error(Object format) {
		error(format, EMPTY_ARGS);
	}

	public void debug(Object format) {
		debug(format, EMPTY_ARGS);
	}
}
