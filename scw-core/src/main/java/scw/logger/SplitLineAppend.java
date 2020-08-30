package scw.logger;

import java.io.IOException;
import java.io.Serializable;

import scw.core.GlobalPropertyFactory;
import scw.util.StringAppend;

public final class SplitLineAppend implements Serializable, StringAppend {
	private static final long serialVersionUID = 1L;
	private static final String DIVIDING_LINE;
	
	static{
		int len = GlobalPropertyFactory.getInstance().getValue("scw.split.line.length", int.class, 20);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len; i++){
			sb.append("-");
		}
		DIVIDING_LINE = sb.toString();
	}
	
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
		if (msg instanceof StringAppend) {
			((StringAppend) msg).appendTo(appendable);
		} else {
			appendable.append(String.valueOf(msg));
		}
		appendable.append(DIVIDING_LINE);
	}
}
