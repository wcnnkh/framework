package io.basc.framework.convert;

public interface Mapper<S, T, E extends Throwable> extends Converter<S, T, E>, Transformer<S, T, E> {
}