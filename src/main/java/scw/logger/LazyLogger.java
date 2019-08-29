package scw.logger;

import scw.core.reflect.ReflectUtils;

/**
 * 懒加载的logger
 * @author shuchaowen
 *
 */
public final class LazyLogger implements Logger {
	private volatile Logger logger;
	private final String name;
	private String placeholder;

	public LazyLogger(Class<?> clazz) {
		this(clazz.getName());
	}

	public LazyLogger(String name) {
		this(name, null);
	}

	public LazyLogger(Class<?> clazz, String placeholder) {
		this(clazz.getName(), placeholder);
	}

	public LazyLogger(String name, String placeholder) {
		this.placeholder = placeholder;
		this.name = name;
	}

	private Logger getLogger() {
		if (logger == null) {
			synchronized (this) {
				if (logger == null) {
					try {
						return (Logger) ReflectUtils.invokeStaticMethod("scw.logger.LoggerFactory", "getLogger",
								new Class<?>[] { String.class, String.class }, name, placeholder);
					} catch (Throwable e) {
						throw new RuntimeException(name, e);
					}
				}
			}
		}
		return logger;
	}

	public String getName() {
		return name;
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

}
