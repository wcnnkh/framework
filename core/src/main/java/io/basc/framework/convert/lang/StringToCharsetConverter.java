package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;

import java.nio.charset.Charset;

public class StringToCharsetConverter implements Converter<String, Charset> {

	public Charset convert(String source) {
		return Charset.forName(source);
	}
	
}
