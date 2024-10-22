package io.basc.framework.util.attribute;

public interface EditableAttributesWrapper<K, V, W extends EditableAttributes<K, V>>
		extends EditableAttributes<K, V>, AttributesWrapper<K, V, W> {

	@Override
	default void setAttributes(Attributes<K, ? extends V> attributes) {
		getSource().setAttributes(attributes);
	}

	@Override
	default void setAttribute(K name, V value) {
		getSource().setAttribute(name, value);
	}

	@Override
	default void removeAttribute(K name) {
		getSource().removeAttribute(name);
	}
}
