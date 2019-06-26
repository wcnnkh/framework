package scw.core;

public interface Append<T> {
	void appendTo(T appendable) throws Exception;
}
