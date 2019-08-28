package scw.logger;

public final class AsyncLogger extends AbstractMyLogger {
	private final AsyncLoggerFactory asyncLoggerFactory;

	public AsyncLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, String name,
			AsyncLoggerFactory asyncLoggerFactory, String placeholder) {
		super(traceEnabled, debugEnabled, infoEnabled, name, placeholder);
		this.asyncLoggerFactory = asyncLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		asyncLoggerFactory.log(message);
	}

}
