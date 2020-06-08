package scw.logger;

import java.io.IOException;
import java.io.Serializable;

import scw.lang.StringAppend;

public final class SplitLineAppend implements Serializable, StringAppend {
	private static final long serialVersionUID = 1L;
	private static final String DIVIDING_LINE = "-------------------";
	private final Object msg;

	public SplitLineAppend(Object msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			appendTo(sb);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public void appendTo(Appendable appendable) throws IOException {
		appendable.append(DIVIDING_LINE);
		appendable.append(msg.toString());
		appendable.append(DIVIDING_LINE);
	}
}
