package run.soeasy.framework.util.attribute;

import run.soeasy.framework.util.collection.Elements;

public interface EditableAttributes<K, V> extends Attributes<K, V> {
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

	void setAttribute(K name, V value);

	void removeAttribute(K name);

	default void setAttributes(Attributes<K, ? extends V> attributes) {
		Elements<? extends K> keys = attributes.getAttributeNames();
		keys.forEach((key) -> setAttribute(key, attributes.getAttribute(key)));
	}
}
