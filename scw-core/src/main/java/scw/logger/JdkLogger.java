package scw.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import scw.util.FormatUtils;

public class JdkLogger extends AbstractLogger {
	private final Logger logger;

	public JdkLogger(Logger logger, String name, String placeholder) {
		super(name, placeholder);
		this.logger = logger;
		registerLevelListener();
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
	public void log(Level level, Throwable e, Object format, Object... args) {
		logger.log(level, e, new Supplier<String>() {

			@Override
			public String get() {
				return FormatUtils.formatPlaceholder(format, getPlaceholder(),
						args);
			}
		});
	}
}
