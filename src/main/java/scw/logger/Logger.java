package scw.logger;

public interface Logger {

	boolean isTraceEnabled();

	void trace(String msg);

	void trace(String format, Object... args);

	void trace(String msg, Throwable t);

	void trace(Throwable t, String msg, Object... args);

	boolean isDebugEnabled();

	void debug(String msg);

	void debug(String format, Object... args);

	void debug(String msg, Throwable t);

	void debug(Throwable t, String format, Object... args);

	boolean isInfoEnabled();

	void info(String msg);

	void info(String format, Object... args);

	void info(String msg, Throwable t);

	void info(Throwable t, String format, Object... args);

	boolean isWarnEnabled();

	void warn(String msg);

	void warn(String format, Object... args);

	void warn(String msg, Throwable t);

	void warn(Throwable t, String format, Object... args);

	boolean isErrorEnabled();

	public void error(String msg);

	public void error(String format, Object... args);

	public void error(String msg, Throwable t);

	void error(Throwable e, String format, Object... args);
}
