package scw.util.attribute;

public interface Attributes<K, V> extends AttributesReadOnly<K, V> {
	void setAttribute(K name, V o);

	void removeAttribute(K name);
}
