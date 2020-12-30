package scw.convert.support;

import java.nio.charset.Charset;

import scw.convert.Converter;

public class StringToCharsetConverter implements Converter<String, Charset> {

	public Charset convert(String source) {
		return Charset.forName(source);
	}
	
}
