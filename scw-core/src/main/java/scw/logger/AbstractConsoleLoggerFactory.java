package scw.logger;

import java.io.IOException;

public abstract class AbstractConsoleLoggerFactory implements ILoggerFactory{

	public Logger getLogger(String name, String placeholder) {
		return new ConsoleLogger(LoggerLevelManager.getInstance().getLevel(name), name, this, placeholder);
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
