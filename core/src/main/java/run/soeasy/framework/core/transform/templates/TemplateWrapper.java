package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.DictionaryWrapper;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

@FunctionalInterface
public interface TemplateWrapper<E extends AccessibleDescriptor, W extends Template<E>>
		extends Template<E>, DictionaryWrapper<Object, E, KeyValue<Object, E>, W> {
	@Override
	default Template<E> asMap() {
		return getSource().asMap();
	}

	@Override
	default Template<E> asArray() {
		return getSource().asArray();
	}
	
	@Override
	default E get(Object key) {
		return getSource().get(key);
	}
}