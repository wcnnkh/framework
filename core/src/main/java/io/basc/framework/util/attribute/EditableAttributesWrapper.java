package io.basc.framework.util.attribute;

public class EditableAttributesWrapper<W extends EditableAttributes<K, V>, K, V> extends AttributesWrapper<W, K, V>
		implements EditableAttributes<K, V> {

	public EditableAttributesWrapper(W targetAttributes) {
		super(targetAttributes);
	}

	public void setAttribute(K name, V o) {
		wrappedTarget.setAttribute(name, o);
	}

	public void removeAttribute(K name) {
		wrappedTarget.removeAttribute(name);
	}
}
