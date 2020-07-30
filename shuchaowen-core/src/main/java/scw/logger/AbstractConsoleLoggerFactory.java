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
		if (message.getLevel().getValue() >= Level.WARN.getValue()) {
			System.err.println(appendable.toString());
		} else {
			System.out.println(appendable.toString());
		}
		if (message.getThrowable() != null) {
			message.getThrowable().printStackTrace();
		}
	}
}
