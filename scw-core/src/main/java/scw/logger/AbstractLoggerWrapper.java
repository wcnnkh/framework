package scw.logger;

import java.util.logging.Level;

public abstract class AbstractLoggerWrapper implements Logger {
	protected abstract Logger getLogger();

	public String getName() {
		return getLogger().getName();
	}

	public boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	public void info(Object format) {
		getLogger().info(format);
	}

	public void info(Object format, Object... args) {
		getLogger().info(format, args);
	}

	public void info(Throwable e, Object format, Object... args) {
		getLogger().info(e, format, args);
	}

	public boolean isTraceEnabled() {
		return getLogger().isTraceEnabled();
	}

	public void trace(Object format) {
		getLogger().trace(format);
	}

	public void trace(Object format, Object... args) {
		getLogger().trace(format, args);
	}

	public void trace(Throwable e, Object format, Object... args) {
		getLogger().trace(e, format, args);
	}

	public boolean isWarnEnabled() {
		return getLogger().isWarnEnabled();
	}

	public void warn(Object format) {
		getLogger().warn(format);
	}

	public void warn(Object format, Object... args) {
		getLogger().warn(format, args);
	}

	public void warn(Throwable e, Object format, Object... args) {
		getLogger().warn(e, format, args);
	}

	public boolean isErrorEnabled() {
		return getLogger().isErrorEnabled();
	}

	public void error(Object format) {
		getLogger().error(format);
	}

	public void error(Object format, Object... args) {
		getLogger().error(format, args);
	}

	public void error(Throwable e, Object format, Object... args) {
		getLogger().error(e, format, args);
	}

	public boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	public void debug(Object format) {
		getLogger().debug(format);
	}

	public void debug(Object format, Object... args) {
		getLogger().debug(format, args);
	}

	public void debug(Throwable e, Object format, Object... args) {
		getLogger().debug(e, format, args);
	}
	
	public boolean isLogEnable(Level level) {
		return getLogger().isLogEnable(level);
	}
	
	public void log(Level level, Object format) {
		getLogger().log(level, format);
	}
	
	public void log(Level level, Object format, Object... args) {
		getLogger().log(level, format, args);
	}
	
	public void log(Level level, Throwable e, Object format, Object... args) {
		getLogger().log(level, e, format, args);
	}
}