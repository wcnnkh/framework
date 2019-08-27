package scw.logger;

public interface Logger {
	static final Object[] EMPTY_ARGS = new Object[0];
	
	String getName();

	boolean isInfoEnabled();

	void info(final Object format);

	void info(final Object format, final Object... args);

	void info(final Throwable e, final Object format, final Object... args);

	boolean isTraceEnabled();

	void trace(final Object format);

	void trace(final Object format, final Object... args);

	void trace(final Throwable e,final Object format,final Object... args);

	boolean isWarnEnabled();

	void warn(final Object format);

	void warn(final Object format,final Object... args);

	void warn(final Throwable e, final Object format, final Object... args);

	boolean isErrorEnabled();

	void error(final Object format);

	void error(final Object format, final Object... args);

	void error(final Throwable e, final Object format, final Object... args);

	boolean isDebugEnabled();

	void debug(final Object format);

	void debug(final Object format, final Object... args);

	void debug(final Throwable e, final Object format, final Object... args);
}
