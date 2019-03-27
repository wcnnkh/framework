package scw.logger.console;

import scw.logger.AbstractLogger;
import scw.logger.Message;

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
