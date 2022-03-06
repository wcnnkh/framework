package io.basc.framework.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.basc.framework.util.FormatUtils;

/**
 * 包装的jdk日志记录器
 * 
 * @author shuchaowen
 *
 */
public class JdkLogger extends CustomLogger {
	private final Logger logger;

	public JdkLogger(Logger logger) {
		this.logger = logger;
		Level level = LoggerFactory.getLevelManager().get().getLevel(logger.getName());
		if (level != null) {
			setLevel(level);
		}
		registerListener();
	}

	public Logger getTargetLogger() {
		return logger;
	}

	@Override
	public Level getLevel() {
		return logger.getLevel();
	}

	@Override
	public void setLevel(Level level) {
		logger.setLevel(level);
		super.setLevel(level);
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public void log(Level level, Throwable e, String format, Object... args) {
		if (!isLoggable(level)) {
			return;
		}

		logger.logp(level, null, null, e, () -> FormatUtils.formatPlaceholder(format, null, args));
	}

	@Override
	public boolean isLoggable(Level level) {
		return logger.isLoggable(level);
	}

	@Override
	public void log(Level level, Throwable e, Supplier<String> msg, Object... args) {
		if (!isLoggable(level)) {
			return;
		}

		logger.logp(level, null, null, e, () -> FormatUtils.formatPlaceholder(msg.get(), null, args));
	}
}
