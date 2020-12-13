package scw.logger;

import scw.event.BasicEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;

public abstract class AbstractLogger implements Logger {
	private static final Object[] EMPTY_ARGS = new Object[0];
	protected final String placeholder;
	private volatile Level level;
	private EventRegistration eventRegistration;

	public AbstractLogger(Level level, String placeholder) {
		this.level = level;
		this.placeholder = placeholder;
	}

	@Override
	protected void finalize() throws Throwable {
		if (eventRegistration != null) {
			eventRegistration.unregister();
		}
		eventRegistration = null;
		super.finalize();
	}

	/**
	 * 注册对日志Level变更的监听
	 * 
	 * @return 如果已经注册过了就返回false, 否则返回true
	 */
	public boolean registerLevelListener() {
		if (eventRegistration != null) {
			// 已经注册过吧
			return false;
		}

		eventRegistration = LoggerLevelManager.getInstance().getEventDispatcher()
				.registerListener(new EventListener<BasicEvent>() {
					public void onEvent(BasicEvent event) {
						Level level = LoggerLevelManager.getInstance().getLevel(getName());
						if (!level.equals(getLevel())) {
							setLevel(level);
						}
					}
				});
		return true;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		//这里使用off是为了任意日志级别都会显示该日志
		log(Level.OFF, "level change [{}]", level);
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
		return level.isGreaterOrEqual(getLevel());
	}
}
