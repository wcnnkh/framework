package io.basc.framework.util.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.actor.EventListener;

public class DynamicLogger implements Logger, Wrapper<Logger> {
	private static final boolean NEED_TO_INFER_CALLER = Boolean
			.getBoolean("io.basc.framework.logger.need.to.infer.caller");

	private volatile Level level;
	private volatile Logger source;
	private volatile boolean needToInferCaller = NEED_TO_INFER_CALLER;

	public DynamicLogger(Logger source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	public boolean isNeedToInferCaller() {
		return needToInferCaller;
	}

	public void setNeedToInferCaller(boolean needToInferCaller) {
		this.needToInferCaller = needToInferCaller;
	}

	private Boolean isNameToClass;

	/**
	 * logger名称是否是一个类名
	 * 
	 * @return
	 */
	public boolean isNameToClass() {
		if (isNameToClass == null) {
			synchronized (this) {
				if (isNameToClass == null) {
					isNameToClass = ClassUtils.isPresent(getName(), null);
				}
			}
		}
		return isNameToClass;
	}

	public void setIsNameToClass(boolean isNameToClass) {
		this.isNameToClass = isNameToClass;
	}

	/**
	 * 性能开销大，不建议使用
	 * 
	 * @return
	 */
	@Nullable
	public StackTraceElement getStackTraceElement() {
		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			if (DynamicLogger.class.getName().equals(stackTraceElement.getClassName())
					|| Logger.class.getName().equals(stackTraceElement.getClassName())) {
				continue;
			}

			// 不可以是Logger的继承类
			Class<?> sourceClass = ClassUtils.getClass(stackTraceElement.getClassName(), Logger.class.getClassLoader());
			if (sourceClass != null && Logger.class.isAssignableFrom(sourceClass)) {
				continue;
			}

			return stackTraceElement;
		}
		return null;
	}

	@Override
	public LogRecord createRecord(Level level, Throwable thrown, String msg, Object... args) {
		LogRecord logRecord = Logger.super.createRecord(level, thrown, msg, args);
		if (isNeedToInferCaller()) {
			StackTraceElement stackTraceElement = getStackTraceElement();
			if (stackTraceElement != null) {
				logRecord.setSourceClassName(stackTraceElement.getClassName());
				logRecord.setSourceMethodName(
						stackTraceElement.getMethodName() + "[" + stackTraceElement.getLineNumber() + "]");
			}
		} else if (isNameToClass()) {
			logRecord.setSourceClassName(logRecord.getLoggerName());
		}
		return logRecord;
	}

	@Override
	public Level getLevel() {
		Level level = this.level;
		if (level == null) {
			level = source.getLevel();
		}
		return level;
	}

	@Override
	public String getName() {
		return source.getName();
	}

	public Logger getSource() {
		return source;
	}

	@Override
	public void log(LogRecord record) {
		if (isLoggable(record.getLevel())) {
			source.log(record);
		}
	}

	@Override
	public void onEvent(LevelManager event) {
		Level oldLevel = getLevel();
		Level newLevel = event.getLevel(getName());
		if (ObjectUtils.equals(oldLevel, newLevel)) {
			return;
		}
		setLevel(newLevel);
	}

	@Override
	public void setLevel(Level level) {
		info("Log level changed from {} to {}", getLevel(), level);
		this.level = level;
		try {
			source.setLevel(level);
		} catch (Throwable e) {
			error(e, "Exception in setting the source logger log level");
		}
	}

	public void setSource(Logger source) {
		Assert.requiredArgument(source != null, "source");
		Logger oldLogger = this.source;
		this.source = source;
		if (level != null) {
			this.source.setLevel(level);
		}
		info("Logger change from {} to {}", oldLogger, this.source);
	}
}
