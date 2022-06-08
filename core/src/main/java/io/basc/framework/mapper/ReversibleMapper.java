package io.basc.framework.mapper;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.ReversibleTransformer;

public interface ReversibleMapper<S, T, E extends Throwable>
		extends Mapper<S, T, E>, ReversibleConverter<S, T, E>, ReversibleTransformer<S, T, E> {
}
