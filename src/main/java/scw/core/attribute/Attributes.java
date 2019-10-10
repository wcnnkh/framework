package scw.core.attribute;

public interface Attributes<T> extends AttributesReadOnly<T> {
	void setAttribute(String name, T o);

	void removeAttribute(String name);
}
