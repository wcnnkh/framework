package scw.logger;

public interface Logger {
	String getName();

	boolean isInfoEnabled();

	void info(String format, Object... args);

	void info(Throwable e, String msg, Object... args);

	boolean isTraceEnabled();

	void trace(String format, Object... args);

	void trace(Throwable e, String msg, Object... args);

	boolean isWarnEnabled();

	void warn(String format, Object... args);

	void warn(Throwable e, String msg, Object... args);

	boolean isErrorEnabled();

	void error(Throwable e, String msg, Object... args);

	void error(String format, Object... args);

	boolean isDebugEnabled();

	void debug(String format, Object... args);

	void debug(Throwable e, String msg, Object... args);
}
