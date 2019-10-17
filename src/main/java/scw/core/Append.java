package scw.core;

import scw.core.annotation.Ignore;

@Ignore
public interface Append<T> {
	void appendTo(T appendable) throws Exception;
}
