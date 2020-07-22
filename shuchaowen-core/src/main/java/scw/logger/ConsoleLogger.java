package scw.logger;

public final class ConsoleLogger extends AbstractMessageLogger {
	private final AbstractConsoleLoggerFactory abstractConsoleLoggerFactory;

	public ConsoleLogger(Level level, String name, AbstractConsoleLoggerFactory abstractConsoleLoggerFactory,
			String placeholder) {
		super(level, name, placeholder);
		this.abstractConsoleLoggerFactory = abstractConsoleLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		abstractConsoleLoggerFactory.log(message);
	}

}
