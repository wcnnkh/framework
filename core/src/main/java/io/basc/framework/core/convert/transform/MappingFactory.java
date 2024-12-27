package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;

public interface MappingFactory<T, K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable> {
	M getMapping(T transform, TypeDescriptor typeDescriptor);
}
