package scw.logger;

import java.io.IOException;

public abstract class AbstractConsoleLoggerFactory extends AbstractILoggerFactory {

	public Logger getLogger(String name, String placeholder) {
		Level level = LoggerLevelUtils.getLevel(name);
		return new ConsoleLogger(level, name, this, placeholder);
	}

	protected abstract void log(Message message);

	protected abstract Appendable createAppendable();

	public void console(Message message) throws IOException {
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
