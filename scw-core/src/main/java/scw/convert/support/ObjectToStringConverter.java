package scw.convert.support;

import scw.convert.Converter;

public class ObjectToStringConverter implements Converter<Object, String> {
	public String convert(Object o) {
		return String.valueOf(o);
	}
}
