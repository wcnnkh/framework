package scw.util;

import java.io.IOException;
import java.io.Serializable;

import scw.lang.Nullable;

public final class PlaceholderFormat implements StringAppend, Serializable{
	private static final long serialVersionUID = 1L;
	private final Object msg;
	private final Object[] args;
	private final String placeholder;

	public PlaceholderFormat(Object msg, @Nullable String placeholder, Object[] args) {
		this.msg = msg;
		this.placeholder = placeholder;
		this.args = args;
	}

	public Object getMsg() {
		return msg;
	}

	public Object[] getArgs() {
		return args;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void appendTo(Appendable appendable) throws IOException {
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
