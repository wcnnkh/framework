package io.basc.framework.convert.lang;

import java.nio.charset.Charset;
import java.util.function.Function;

public class StringToCharsetConverter implements Function<String, Charset> {

	public Charset apply(String source) {
		return Charset.forName(source);
	}

}
