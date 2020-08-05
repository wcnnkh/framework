package scw.logger;

public final class ConsoleLogger extends AbstractLogger {
	private final AbstractConsoleLoggerFactory abstractConsoleLoggerFactory;
	private final String name;

	public ConsoleLogger(Level level, String name,
			AbstractConsoleLoggerFactory abstractConsoleLoggerFactory,
			String placeholder) {
		super(level, placeholder);
		this.name = name;
		this.abstractConsoleLoggerFactory = abstractConsoleLoggerFactory;
		registerLevelListener();
	}

	public void log(Level level, Throwable e, Object format, Object... args) {
		if (isLogEnable(level)) {
			Message message = new Message(level, name, format, args, e,
					getPlaceholder());
			abstractConsoleLoggerFactory.log(message);
		}
	}

	public String getName() {
		return name;
	}
}
