package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import run.soeasy.framework.core.ObjectUtils;

public interface Logger {

	default LogRecord createRecord(Level level, Throwable thrown, String msg, Object... args) {
		LogRecord logRecord = new LogRecord(level, msg);
		if (thrown != null) {
			logRecord.setThrown(thrown);
		}

		if (args != null) {
			logRecord.setParameters(args);
		}

		logRecord.setLoggerName(getName());
		return logRecord;
	}

	default void debug(String msg) {
		log(createRecord(CustomLevel.DEBUG, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void debug(String msg, Object... args) {
		log(createRecord(CustomLevel.DEBUG, null, msg, args));
	}

	default void debug(Throwable e, String msg) {
		log(createRecord(CustomLevel.DEBUG, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void debug(Throwable e, String msg, Object... args) {
		log(createRecord(CustomLevel.DEBUG, e, msg, args));
	}

	default void error(String msg) {
		log(createRecord(CustomLevel.ERROR, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void error(String msg, Object... args) {
		log(createRecord(CustomLevel.ERROR, null, msg, args));
	}

	default void error(Throwable e, String msg) {
		log(createRecord(CustomLevel.ERROR, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void error(Throwable e, String msg, Object... args) {
		log(createRecord(CustomLevel.ERROR, e, msg, args));
	}

	String getName();

	default void info(String msg) {
		log(createRecord(CustomLevel.INFO, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void info(String msg, Object... args) {
		log(createRecord(CustomLevel.INFO, null, msg, args));
	}

	default void info(Throwable e, String msg) {
		log(createRecord(CustomLevel.INFO, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void info(Throwable e, String msg, Object... args) {
		log(createRecord(CustomLevel.INFO, e, msg, args));
	}

	default boolean isDebugEnabled() {
		return isLoggable(CustomLevel.DEBUG);
	}

	default boolean isErrorEnabled() {
		return isLoggable(CustomLevel.ERROR);
	}

	default boolean isInfoEnabled() {
		return isLoggable(CustomLevel.INFO);
	}

	boolean isLoggable(Level level);

	default boolean isTraceEnabled() {
		return isLoggable(CustomLevel.TRACE);
	}

	default boolean isWarnEnabled() {
		return isLoggable(CustomLevel.WARN);
	}

	default void log(Level level, String msg) {
		log(createRecord(level, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void log(Level level, String msg, Object... args) {
		log(createRecord(level, null, msg, args));
	}

	default void log(Level level, Throwable e, String msg) {
		log(createRecord(level, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void log(Level level, Throwable e, String msg, Object... args) {
		log(createRecord(level, e, msg, args));
	}

	void log(LogRecord record);

	default void trace(String msg) {
		log(createRecord(CustomLevel.TRACE, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void trace(String msg, Object... args) {
		log(createRecord(CustomLevel.TRACE, null, msg, args));
	}

	default void trace(Throwable e, String msg) {
		log(createRecord(CustomLevel.TRACE, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void trace(Throwable e, String msg, Object... args) {
		log(createRecord(CustomLevel.TRACE, e, msg, args));
	}

	default void warn(String msg) {
		log(createRecord(CustomLevel.WARN, null, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void warn(String msg, Object... args) {
		log(createRecord(CustomLevel.WARN, null, msg, args));
	}

	default void warn(Throwable e, String msg) {
		log(createRecord(CustomLevel.WARN, e, msg, ObjectUtils.EMPTY_ARRAY));
	}

	default void warn(Throwable e, String msg, Object... args) {
		log(createRecord(CustomLevel.WARN, e, msg, args));
	}
}
