package scw.logger;

import scw.core.UnsafeStringBuffer;

public abstract class AbstractMyLoggerFactory extends AbstractILoggerFactory {

	public Logger getLogger(String name, String placeholder) {
		Level level = LoggerLevelUtils.getLevel(name);
		return new MyLogger(level, name, this, placeholder);
	}

	public abstract void log(Message message);

	public void console(UnsafeStringBuffer unsafeStringBuffer, Message message) throws Exception {
		String msg = message.toString(unsafeStringBuffer);
		switch (message.getLevel()) {
		case ERROR:
		case WARN:
			System.err.println(msg);
			break;
		default:
			System.out.println(msg);
			break;
		}

		if (message.getThrowable() != null) {
			message.getThrowable().printStackTrace();
		}
	}
}
