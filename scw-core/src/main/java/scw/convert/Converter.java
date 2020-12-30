package scw.convert;

import scw.lang.Ignore;

@Ignore
public interface Converter<S, T> {
	T convert(S o);
}
