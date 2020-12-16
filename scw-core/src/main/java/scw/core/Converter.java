package scw.core;

import scw.lang.Ignore;

@Ignore
public interface Converter<O, T> {
	T convert(O o);
}
