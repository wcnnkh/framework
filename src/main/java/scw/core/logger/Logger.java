package scw.core.logger;

public interface Logger {

	boolean isTraceEnabled();

	void trace(String msg);

	void trace(String format, Object... args);

	void trace(String msg, Throwable t);

	boolean isDebugEnabled();

	void debug(String msg);

	void debug(String format, Object... args);

	void debug(String msg, Throwable t);

	boolean isInfoEnabled();

	void info(String msg);

	void info(String format, Object... args);

	void info(String msg, Throwable t);

	boolean isWarnEnabled();

	void warn(String msg);

	void warn(String format, Object... args);

	void warn(String msg, Throwable t);

	boolean isErrorEnabled();

	void error(String msg);

	void error(String format, Object... args);

	void error(String msg, Throwable t);

}
