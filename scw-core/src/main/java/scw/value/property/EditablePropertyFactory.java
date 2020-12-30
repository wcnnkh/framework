package scw.value.property;

import scw.value.property.BasePropertyFactory;

public interface EditablePropertyFactory extends BasePropertyFactory {
	boolean put(String key, Object value);

	boolean remove(String key);
}
