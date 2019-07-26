package scw.logger;

public final class AsyncLogger extends AbstractLogger {
	private final AsyncLoggerFactory asyncLoggerFactory;

	public AsyncLogger(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, boolean warnEnabled,
			boolean errorEnabled, String tag, AsyncLoggerFactory asyncLoggerFactory) {
		super(traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, tag, null);
		this.asyncLoggerFactory = asyncLoggerFactory;
	}

	@Override
	protected void log(Message message) {
		asyncLoggerFactory.log(message);
	}

}
