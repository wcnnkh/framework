package scw.logger;

import java.util.SortedMap;
import java.util.logging.Level;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.Nullable;

public abstract class AbstractLogger implements Logger {
	private static final Object[] EMPTY_ARGS = new Object[0];
	protected final String placeholder;
	private volatile Level level;
	private final String name;
	private EventRegistration eventRegistration;

	public AbstractLogger(String name, @Nullable String placeholder) {
		this.level = LoggerLevelManager.getInstance().getLevel(name);
		this.name = name;
		this.placeholder = placeholder;
	}

	@Override
	public String getName() {
		return name;
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
	public synchronized boolean registerLevelListener() {
		if (eventRegistration != null) {
			// 已经注册过吧
			return false;
		}

		eventRegistration = LoggerLevelManager
				.getInstance()
				.getRegistry()
				.registerListener(
						new EventListener<ChangeEvent<SortedMap<String, Level>>>() {

							public void onEvent(
									ChangeEvent<SortedMap<String, Level>> event) {
								Level level = LoggerLevelManager.getInstance()
										.getLevel(getName());
								if (!level.equals(getLevel())) {
									setLevel(level);
								}
							}
						});
		return true;
	}

	@Nullable
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		// 这里使用off是为了任意日志级别都会显示该日志
		log(Level.OFF, "Level [{}] change to [{}]", getLevel(), level);
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
		log(CustomLevel.INFO, e, format, args);
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
		log(CustomLevel.TRACE, e, format, args);
	}

	public boolean isTraceEnabled() {
		return isLogEnable(CustomLevel.TRACE);
	}

	public boolean isWarnEnabled() {
		return isLogEnable(CustomLevel.WARN);
	}

	public void warn(Object format) {
		warn(format, EMPTY_ARGS);
	}

	public void warn(Object format, Object... args) {
		warn(null, format, args);
	}

	public void warn(Throwable e, Object format, Object... args) {
		log(CustomLevel.WARN, e, format, args);
	}

	public boolean isDebugEnabled() {
		return isLogEnable(CustomLevel.DEBUG);
	}

	public void debug(Object format) {
		debug(format, EMPTY_ARGS);
	}

	public void debug(Object format, Object... args) {
		debug(null, format, args);
	}

	public void debug(Throwable e, Object format, Object... args) {
		log(CustomLevel.DEBUG, e, format, args);
	}

	public boolean isErrorEnabled() {
		return isLogEnable(CustomLevel.ERROR);
	}

	public void error(Object format) {
		error(format, EMPTY_ARGS);
	}

	public void error(Object format, Object... args) {
		error(null, format, args);
	}

	public void error(Throwable e, Object format, Object... args) {
		log(CustomLevel.ERROR, e, format, args);
	}

	public void log(Level level, Object format) {
		log(level, format, EMPTY_ARGS);
	}

	public void log(Level level, Object format, Object... args) {
		log(level, null, format, args);
	}

	public boolean isLogEnable(Level level) {
		return CustomLevel.isGreaterOrEqual(level, getLevel());
	}
}
