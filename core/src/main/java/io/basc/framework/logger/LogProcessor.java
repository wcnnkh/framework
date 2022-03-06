package io.basc.framework.logger;

import java.util.function.Supplier;

import io.basc.framework.util.ObjectUtils;

public interface LogProcessor {
	boolean isEnabled();

	default void log(String msg) {
		log(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(Supplier<String> msg) {
		log(msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(String msg, Object... args) {
		log(null, msg, args);
	}

	default void log(Supplier<String> msg, Object... args) {
		log(null, msg, args);
	}

	default void log(Throwable e, String msg) {
		log(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	default void log(Throwable e, Supplier<String> msg) {
		log(e, msg, ObjectUtils.EMPTY_ARRAY);
	}

	void log(Throwable e, String msg, Object... args);

	void log(Throwable e, Supplier<String> msg, Object... args);
}
