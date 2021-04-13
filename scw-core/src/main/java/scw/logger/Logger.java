package scw.logger;

import java.util.logging.Level;

public interface Logger {
	String getName();

	boolean isInfoEnabled();

	void info(Object format);

	void info(Object format, Object... args);

	void info(Throwable e, Object format, Object... args);

	boolean isTraceEnabled();

	void trace(Object format);

	void trace(Object format, Object... args);

	void trace(Throwable e, Object format, Object... args);

	boolean isWarnEnabled();

	void warn(Object format);

	void warn(Object format, Object... args);

	void warn(Throwable e, Object format, Object... args);

	boolean isErrorEnabled();

	void error(Object format);

	void error(Object format, Object... args);

	void error(Throwable e, Object format, Object... args);

	boolean isDebugEnabled();

	void debug(Object format);

	void debug(Object format, Object... args);

	void debug(Throwable e, Object format, Object... args);

	boolean isLogEnable(Level level);

	void log(Level level, Object format);

	void log(Level level, Object format, Object... args);

	void log(Level level, Throwable e, Object format, Object... args);
}
