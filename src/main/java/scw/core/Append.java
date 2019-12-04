package scw.core;

import scw.lang.Ignore;

@Ignore
public interface Append<T> {
	void appendTo(T appendable) throws Exception;
}
