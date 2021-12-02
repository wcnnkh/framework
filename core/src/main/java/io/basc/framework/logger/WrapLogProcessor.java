package io.basc.framework.logger;

import java.util.logging.Level;

class WrapLogProcessor implements LogProcessor {
	private final Logger logger;
	private final Level level;

	public WrapLogProcessor(Logger logger, Level level) {
		this.logger = logger;
		this.level = level;
	}

	@Override
	public boolean isEnabled() {
		return logger.isLoggable(level);
	}

	@Override
	public void log(Throwable e, String msg, Object... args) {
		logger.log(level, e, msg, args);
	}

}
