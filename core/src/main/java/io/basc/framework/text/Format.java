package io.basc.framework.text;

import java.io.IOException;
import java.io.StringReader;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

public interface Format<T> {

	default String format(T source) {
		return format(source, new FieldPosition(0));
	}

	default void format(T source, Appendable target) throws IOException {
		format(source, target, new FieldPosition(0));
	}

	void format(T source, Appendable target, FieldPosition position) throws IOException;

	default String format(T source, FieldPosition position) {
		StringBuilder sb = new StringBuilder();
		try {
			format(source, sb, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	default T parse(Readable source) throws IOException, ParseException {
		ParsePosition pos = new ParsePosition(0);
		T result = parse(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("Format.parseObject(String) failed", pos.getErrorIndex());
		}
		return result;
	}

	T parse(Readable source, ParsePosition position) throws IOException;

	default T parse(String source) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		T result = parse(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("Format.parseObject(String) failed", pos.getErrorIndex());
		}
		return result;
	}

	default T parse(String source, ParsePosition position) {
		StringReader reader = new StringReader(source);
		try {
			return parse(reader, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}
}