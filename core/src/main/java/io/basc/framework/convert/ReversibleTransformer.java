package io.basc.framework.convert;

public interface ReversibleTransformer<S, T, E extends Throwable> extends Transformer<S, T, E>, ReverseTransformer<T, S, E> {
}
