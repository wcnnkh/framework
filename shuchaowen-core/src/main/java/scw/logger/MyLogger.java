package scw.logger;

public final class MyLogger extends AbstractMyLogger {
	private final AbstractMyLoggerFactory abstractMyLoggerFactory;

	public MyLogger(Level level, String name, AbstractMyLoggerFactory abstractMyLoggerFactory,
			String placeholder) {
		super(level, name, placeholder);
		this.abstractMyLoggerFactory = abstractMyLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		abstractMyLoggerFactory.log(message);
	}

}
