package io.basc.framework.net.text;

import java.io.IOException;
import java.io.StringReader;

public interface Format<T> {

	void format(T source, Appendable target) throws IOException;

	default String format(T source) {
		StringBuilder sb = new StringBuilder();
		try {
			format(source, sb);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	T parse(Readable source) throws IOException;

	default T parse(String source) {
		StringReader reader = new StringReader(source);
		try {
			return parse(reader);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}
}