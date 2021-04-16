package scw.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import scw.util.FormatUtils;

public class JdkLogger extends CustomLogger {
	private final Logger logger;

	public JdkLogger(Logger logger) {
		this.logger = logger;
		registerListener();
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
		if(!isLoggable(level)) {
			return ;
		}
		
		logger.logp(level, null, null, e, new Supplier<String>() {

			@Override
			public String get() {
				return FormatUtils.formatPlaceholder(format, null,
						args);
			}
		});
	}

	@Override
	public boolean isLoggable(Level level) {
		return super.isLoggable(level) && logger.isLoggable(level);
	}
}
