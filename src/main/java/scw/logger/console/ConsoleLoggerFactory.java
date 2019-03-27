package scw.logger.console;

import scw.logger.AbstractLoggerFactory;
import scw.logger.Logger;
import scw.logger.Message;

public final class ConsoleLoggerFactory extends AbstractLoggerFactory {

	@Override
	protected void out(Message message) {
		console(message);
	}

	public Logger getLogger(String name) {
		return new ConsoleLogger(true, true, true, true, true, name, this);
	}
}
