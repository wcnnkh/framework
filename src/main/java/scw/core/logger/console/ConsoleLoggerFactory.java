package scw.core.logger.console;

import scw.core.logger.Logger;
import scw.core.logger.Message;

public final class ConsoleLoggerFactory extends AbstractLoggerFactory {

	@Override
	protected void out(Message message) {
		console(message);
	}

	public Logger getLogger(String name) {
		return new ConsoleLogger(true, true, true, true, true, name, this);
	}
}
