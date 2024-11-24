package io.basc.framework.util;

import java.io.Serializable;
import java.util.function.Supplier;

public class SplitLine implements Supplier<String>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Object body;
	private String separator = "-";
	private int length = 20;

	public SplitLine() {
		this(null);
	}

	public SplitLine(Object body) {
		this.body = body;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Object getBody() {
		return body;
	}

	@Override
	public String get() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(separator);
		}

		String line = sb.toString();
		sb.append(body);
		sb.append(line);

		return sb.toString();
	}
}
