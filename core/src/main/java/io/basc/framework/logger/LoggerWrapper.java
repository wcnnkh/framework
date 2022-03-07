package io.basc.framework.logger;

import java.util.function.Supplier;
import java.util.logging.Level;

public interface LoggerWrapper extends Logger{
	Logger getSource();
	
	@Override
	default String getName() {
		return getSource().getName();
	}
	
	@Override
	default boolean isLoggable(Level level) {
		return getSource().isLoggable(level);
	}
	
	@Override
	default void log(Level level, Throwable e, String msg, Object... args) {
		getSource().log(level, e, msg, args);
	}
	
	@Override
	default void log(Level level, Throwable e, Supplier<String> msg, Object... args) {
		getSource().log(level, e, msg, args);
	}
}
