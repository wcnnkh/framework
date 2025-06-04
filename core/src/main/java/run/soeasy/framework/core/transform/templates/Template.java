package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.Dictionary;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

@FunctionalInterface
public interface Template<E extends AccessibleDescriptor> extends Dictionary<Object, E, KeyValue<Object, E>> {
	@Override
	default Template<E> asArray(boolean uniqueness) {
		return new ArrayTemplate<>(this, uniqueness);
	}

	@Override
	default Template<E> asMap(boolean uniqueness) {
		return new MapTemplate<>(this, uniqueness);
	}

	default E get(Object key) throws NoUniqueElementException {
		if (key instanceof Number) {
			KeyValue<Object, E> element = getElement(((Number) key).intValue());
			return element == null ? null : element.getValue();
		} else {
			Elements<E> values = getValues(key);
			if (values == null) {
				return null;
			}
			return values.getUnique();
		}
	}
}
