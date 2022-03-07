package io.basc.framework.logger;

import java.util.function.Supplier;
import java.util.logging.Level;

import io.basc.framework.util.ObjectUtils;

public interface Logger {
	String getName();

	default boolean isInfoEnabled() {
		return isLoggable(CustomLevel.INFO);
	}

	default void info(String msg) {
		info(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void info(Supplier<String> msg) {
		info(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void info(String msg, Object... args) {
		info(null, msg, args);
	}

	default void info(Supplier<String> msg, Object... args) {
		info(null, msg, args);
	}

	default void info(Throwable e, String msg) {
		info(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void info(Throwable e, Supplier<String> msg) {
		info(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void info(Throwable e, String msg, Object... args) {
		log(CustomLevel.INFO, e, msg, args);
	}

	default void info(Throwable e, Supplier<String> msg, Object... args) {
		log(CustomLevel.INFO, e, msg, args);
	}

	default boolean isTraceEnabled() {
		return isLoggable(CustomLevel.TRACE);
	}

	default void trace(String msg) {
		trace(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void trace(Supplier<String> msg) {
		trace(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void trace(String msg, Object... args) {
		trace(null, msg, args);
	}

	default void trace(Supplier<String> msg, Object... args) {
		trace(null, msg, args);
	}

	default void trace(Throwable e, String msg) {
		trace(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void trace(Throwable e, Supplier<String> msg) {
		trace(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void trace(Throwable e, String msg, Object... args) {
		log(CustomLevel.TRACE, e, msg, args);
	}

	default void trace(Throwable e, Supplier<String> msg, Object... args) {
		log(CustomLevel.TRACE, e, msg, args);
	}

	default boolean isWarnEnabled() {
		return isLoggable(CustomLevel.WARN);
	}

	default void warn(String msg) {
		warn(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void warn(Supplier<String> msg) {
		warn(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void warn(String msg, Object... args) {
		warn(null, msg, args);
	}

	default void warn(Supplier<String> msg, Object... args) {
		warn(null, msg, args);
	}

	default void warn(Throwable e, String msg) {
		warn(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void warn(Throwable e, Supplier<String> msg) {
		warn(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void warn(Throwable e, String msg, Object... args) {
		log(CustomLevel.WARN, e, msg, args);
	}

	default void warn(Throwable e, Supplier<String> msg, Object... args) {
		log(CustomLevel.WARN, e, msg, args);
	}

	default boolean isErrorEnabled() {
		return isLoggable(CustomLevel.ERROR);
	}

	default void error(String msg) {
		error(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void error(Supplier<String> msg) {
		error(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void error(String msg, Object... args) {
		error(null, msg, args);
	}

	default void error(Supplier<String> msg, Object... args) {
		error(null, msg, args);
	}

	default void error(Throwable e, String msg) {
		error(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void error(Throwable e, Supplier<String> msg) {
		error(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void error(Throwable e, String msg, Object... args) {
		log(CustomLevel.ERROR, e, msg, args);
	}

	default void error(Throwable e, Supplier<String> msg, Object... args) {
		log(CustomLevel.ERROR, e, msg, args);
	}

	default boolean isDebugEnabled() {
		return isLoggable(CustomLevel.DEBUG);
	}

	default void debug(String msg) {
		debug(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void debug(Supplier<String> msg) {
		debug(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void debug(String msg, Object... args) {
		debug(null, msg, args);
	}

	default void debug(Supplier<String> msg, Object... args) {
		debug(null, msg, args);
	}

	default void debug(Throwable e, String msg) {
		debug(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void debug(Throwable e, Supplier<String> msg) {
		debug(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void debug(Throwable e, String msg, Object... args) {
		log(CustomLevel.DEBUG, e, msg, args);
	}

	default void debug(Throwable e, Supplier<String> msg, Object... args) {
		log(CustomLevel.DEBUG, e, msg, args);
	}

	boolean isLoggable(Level level);

	default void log(Level level, String msg) {
		log(level, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(Level level, Supplier<String> msg) {
		log(level, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(Level level, String msg, Object... args) {
		log(level, null, msg, args);
	}

	default void log(Level level, Supplier<String> msg, Object... args) {
		log(level, null, msg, args);
	}

	default void log(Level level, Throwable e, String msg) {
		log(level, e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(Level level, Throwable e, Supplier<String> msg) {
		log(level, e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	void log(Level level, Throwable e, String msg, Object... args);

	void log(Level level, Throwable e, Supplier<String> msg, Object... args);

	default LogProcessor toInfoProcessor() {
		return toProcessor(CustomLevel.INFO);
	}

	default LogProcessor toDebugProcessor() {
		return toProcessor(CustomLevel.DEBUG);
	}

	default LogProcessor toTraceProcessor() {
		return toProcessor(CustomLevel.TRACE);
	}

	default LogProcessor toProcessor(Level level) {
		return new WrapLogProcessor(this, level);
	}
}
