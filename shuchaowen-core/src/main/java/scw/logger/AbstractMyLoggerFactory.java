package scw.logger;

public abstract class AbstractMyLoggerFactory extends AbstractILoggerFactory {

	public Logger getLogger(String name, String placeholder) {
		Level level = LoggerLevelUtils.getLevel(name);
		return new MyLogger(level, name, this, placeholder);
	}

	protected abstract void log(Message message);

	protected abstract Appendable createAppendable();

	public void console(Message message) throws Exception {
		Appendable appendable = createAppendable();
		message.appendTo(appendable);
		switch (message.getLevel()) {
		case ERROR:
		case WARN:
			System.err.println(appendable.toString());
			break;
		default:
			System.out.println(appendable.toString());
			break;
		}

		if (message.getThrowable() != null) {
			message.getThrowable().printStackTrace();
		}
	}
}
