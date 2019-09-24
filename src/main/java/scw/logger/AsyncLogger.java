package scw.logger;

public final class AsyncLogger extends AbstractMyLogger {
	private final AsyncLoggerFactory asyncLoggerFactory;

	public AsyncLogger(Level level, String name, AsyncLoggerFactory asyncLoggerFactory, String placeholder) {
		super(level, name, placeholder);
		this.asyncLoggerFactory = asyncLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		asyncLoggerFactory.log(message);
	}

}
