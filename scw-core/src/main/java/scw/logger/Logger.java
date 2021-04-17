package scw.logger;

import java.util.logging.Level;

public interface Logger{
	Object[] EMPTY_ARGS = new Object[0];
	
	String getName();
	
	default boolean isInfoEnabled() {
		return isLoggable(CustomLevel.INFO);
	}

	default void info(String msg) {
		info(msg, EMPTY_ARGS);
	}

	default void info(String msg, Object... args) {
		info(null, msg, args);
	}

	default void info(Throwable e, String msg) {
		info(e, msg, EMPTY_ARGS);
	}

	default void info(Throwable e, String msg, Object... args) {
		log(CustomLevel.INFO, e, msg, args);
	}

	default boolean isTraceEnabled() {
		return isLoggable(CustomLevel.TRACE);
	}

	default void trace(String msg) {
		trace(msg, EMPTY_ARGS);
	}

	default void trace(String msg, Object... args) {
		trace(null, msg, args);
	}

	default void trace(Throwable e, String msg) {
		trace(e, msg, EMPTY_ARGS);
	}

	default void trace(Throwable e, String msg, Object... args) {
		log(CustomLevel.TRACE, e, msg, args);
	}

	default boolean isWarnEnabled() {
		return isLoggable(CustomLevel.WARN);
	}

	default void warn(String msg) {
		warn(msg, EMPTY_ARGS);
	}

	default void warn(String msg, Object... args) {
		warn(null, msg, args);
	}

	default void warn(Throwable e, String msg) {
		warn(e, msg, EMPTY_ARGS);
	}

	default void warn(Throwable e, String msg, Object... args) {
		log(CustomLevel.WARN, e, msg, args);
	}

	default boolean isErrorEnabled() {
		return isLoggable(CustomLevel.ERROR);
	}

	default void error(String msg) {
		error(msg, EMPTY_ARGS);
	}

	default void error(String msg, Object... args) {
		error(null, msg, args);
	}

	default void error(Throwable e, String msg) {
		error(e, msg, EMPTY_ARGS);
	}

	default void error(Throwable e, String msg, Object... args) {
		log(CustomLevel.ERROR, e, msg, args);
	}

	default boolean isDebugEnabled() {
		return isLoggable(CustomLevel.DEBUG);
	}

	default void debug(String msg) {
		debug(msg, EMPTY_ARGS);
	}

	default void debug(String msg, Object... args) {
		debug(null, msg, args);
	}

	default void debug(Throwable e, String msg) {
		debug(e, msg, EMPTY_ARGS);
	}

	default void debug(Throwable e, String msg, Object... args) {
		log(CustomLevel.DEBUG, e, msg, args);
	}

	boolean isLoggable(Level level);

	default void log(Level level, String msg) {
		log(level, msg, EMPTY_ARGS);
	}
	
	default void log(Level level, String msg, Object... args) {
		log(level, null, msg, args);
	}

	default void log(Level level, Throwable e, String msg) {
		log(level, e, msg, EMPTY_ARGS);
	}
	
	void log(Level level, Throwable e, String msg, Object... args);
}
