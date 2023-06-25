package io.basc.framework.convert.strings;

import java.nio.charset.Charset;
import java.util.function.Function;

public class StringToCharset implements Function<String, Charset> {
	public static final StringToCharset DEFAULT = new StringToCharset();

	public Charset apply(String source) {
		return Charset.forName(source);
	}

}
