package io.basc.framework.value;

public interface EditablePropertyFactory extends PropertyFactory {
	Value put(String key, Value value);

	Value put(String key, Object value);

	Value remove(String key);
}
