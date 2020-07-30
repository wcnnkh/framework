package scw.logger;

public abstract class AbstractLogger implements Logger {
	private static final Object[] EMPTY_ARGS = new Object[0];
	protected final String placeholder;
	private volatile Level level;

	public AbstractLogger(Level level, String placeholder) {
		this.level = level;
		this.placeholder = placeholder;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void info(Object format) {
		info(format, EMPTY_ARGS);
	}

	public void info(Object format, Object... args) {
		info(null, format, args);
	}

	public void info(Throwable e, Object format, Object... args) {
		log(Level.INFO, e, format, args);
	}

	public boolean isInfoEnabled() {
		return isLogEnable(Level.INFO);
	}

	public void trace(Object format) {
		trace(format, EMPTY_ARGS);
	}

	public void trace(Object format, Object... args) {
		trace(null, format, args);
	}

	public void trace(Throwable e, Object format, Object... args) {
		log(Level.TRACE, e, format, args);
	}

	public boolean isTraceEnabled() {
		return isLogEnable(Level.TRACE);
	}

	public boolean isWarnEnabled() {
		return isLogEnable(Level.WARN);
	}

	public void warn(Object format) {
		warn(format, EMPTY_ARGS);
	}

	public void warn(Object format, Object... args) {
		warn(null, format, args);
	}

	public void warn(Throwable e, Object format, Object... args) {
		log(Level.WARN, e, format, args);
	}

	public boolean isDebugEnabled() {
		return isLogEnable(Level.DEBUG);
	}

	public void debug(Object format) {
		debug(format, EMPTY_ARGS);
	}

	public void debug(Object format, Object... args) {
		debug(null, format, args);
	}

	public void debug(Throwable e, Object format, Object... args) {
		log(Level.DEBUG, e, format, args);
	}

	public boolean isErrorEnabled() {
		return isLogEnable(Level.ERROR);
	}

	public void error(Object format) {
		error(format, EMPTY_ARGS);
	}

	public void error(Object format, Object... args) {
		error(null, format, args);
	}

	public void error(Throwable e, Object format, Object... args) {
		log(Level.ERROR, e, format, args);
	}

	public void log(Level level, Object format) {
		log(level, format, EMPTY_ARGS);
	}

	public void log(Level level, Object format, Object... args) {
		log(level, null, format, args);
	}

	public boolean isLogEnable(Level level) {
		return this.level.isEnable(level);
	}
}
