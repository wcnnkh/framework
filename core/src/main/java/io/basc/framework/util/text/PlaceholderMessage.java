package io.basc.framework.util.text;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

import io.basc.framework.util.AppendTo;
import io.basc.framework.util.FormatUtils;

public final class PlaceholderMessage implements AppendTo, Serializable, Supplier<String> {
	private static final long serialVersionUID = 1L;
	private final Object msg;
	private final Object[] args;
	private final String placeholder;

	public PlaceholderMessage(Object msg, String placeholder, Object[] args) {
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
	public String get() {
		return toString();
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
