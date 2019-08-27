package scw.logger;

import scw.core.utils.FormatUtils;
import scw.core.utils.StringAppend;

public final class DefaultLoggerFormatAppend implements StringAppend {
	private final Object msg;
	private final Object[] args;
	private final String placeholder;

	public DefaultLoggerFormatAppend(Object msg, String placeholder, Object[] args) {
		this.msg = msg;
		this.placeholder = placeholder;
		this.args = args;
	}

	public void appendTo(Appendable appendable) throws Exception {
		FormatUtils.formatPlaceholder(appendable, msg, placeholder, args);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			appendTo(sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
