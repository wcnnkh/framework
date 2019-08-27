package scw.logger;

public final class ConsoleLoggerFactory extends AsyncLoggerFactory {

	public ConsoleLoggerFactory() {
		super("scw-logger");
	}

	public Logger getLogger(String name) {
		return new AsyncLogger(true, true, true, name, this);
	}
}
