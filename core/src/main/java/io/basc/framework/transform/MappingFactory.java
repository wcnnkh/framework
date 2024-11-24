package io.basc.framework.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Transformer;

public interface MappingFactory<T, P extends Property, E extends Throwable> extends Transformer<T, T, E> {
	Mapping<P> getMapping(T transform, TypeDescriptor typeDescriptor);
}
