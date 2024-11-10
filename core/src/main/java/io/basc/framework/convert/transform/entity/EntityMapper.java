package io.basc.framework.convert.transform.entity;

import io.basc.framework.convert.transform.Accessor;
import io.basc.framework.convert.transform.Mapper;
import io.basc.framework.convert.transform.Mapping;

public interface EntityMapper<T, E extends Throwable>
		extends Mapper<T, T, E>, EntityTransformer<Mapping<Object, ? extends Accessor>, Object, Accessor, T, E> {

}
