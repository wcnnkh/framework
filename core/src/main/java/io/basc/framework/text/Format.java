package io.basc.framework.text;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

public interface Format<T> {
	default void format(T source, Appendable target) throws IOException {
		format(source, target, new FieldPosition(0));
	}

	void format(T source, Appendable target, FieldPosition position) throws IOException;

	T parse(Readable source, ParsePosition position) throws IOException, ParseException;
}