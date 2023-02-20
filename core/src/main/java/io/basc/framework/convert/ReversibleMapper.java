package io.basc.framework.convert;

public interface ReversibleMapper<S, T, E extends Throwable>
		extends Mapper<S, T, E>, ReversibleConverter<S, T, E>, ReversibleTransformer<S, T, E> {
}
