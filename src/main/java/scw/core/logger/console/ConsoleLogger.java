package scw.core.logger.console;

import scw.core.logger.AbstractLogger;
import scw.core.logger.Message;

public final class ConsoleLogger extends AbstractLogger {
	private final ConsoleLoggerFactory consoleLoggerFactory;

	public ConsoleLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, boolean warnEnabled,
			boolean errorEnabled, String tag, ConsoleLoggerFactory consoleLoggerFactory) {
		super(traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, tag);
		this.consoleLoggerFactory = consoleLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		consoleLoggerFactory.log(message);
	}

}
