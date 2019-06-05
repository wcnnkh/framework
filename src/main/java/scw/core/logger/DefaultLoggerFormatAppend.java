package scw.core.logger;

import java.io.IOException;

public final class DefaultLoggerFormatAppend implements LoggerAppend {
	private final String msg;
	private final Object[] args;
	private final String placeholder;

	public DefaultLoggerFormatAppend(String msg, String placeholder, Object[] args) {
		this.msg = msg;
		this.placeholder = placeholder;
		this.args = args;
	}

	public void appendLogger(Appendable appendable) throws IOException {
		LoggerUtils.loggerAppend(appendable, msg, placeholder, args);
	}

}
