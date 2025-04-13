package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.type.ClassUtils;

@Getter
@Setter
public abstract class AbstractLogger implements Logger {
	private static final boolean NEED_TO_INFER_CALLER = Boolean
			.getBoolean("io.basc.framework.logger.need.to.infer.caller");
	private Boolean isNameToClass;
	private volatile Level level;
	private volatile boolean needToInferCaller = NEED_TO_INFER_CALLER;

	public void setLevel(Level level) {
		if (this.level != level) {
			info("Log level changed from {} to {}", this.level, level);
		}
		this.level = level;
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

	/**
	 * 性能开销大，不建议使用
	 * 
	 * @return
	 */
	public StackTraceElement getStackTraceElement() {
		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			// TODO 是否要忽略AbstractLogger
			if (FacadeLogger.class.getName().equals(stackTraceElement.getClassName())
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
	public boolean isLoggable(Level level) {
		Level acceptLevel = getLevel();
		if (acceptLevel == null) {
			return true;
		}

		return CustomLevel.isGreaterOrEqual(level, acceptLevel);
	}

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
}
