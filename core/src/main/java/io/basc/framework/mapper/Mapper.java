package io.basc.framework.mapper;

import io.basc.framework.convert.Converter;

public interface Mapper<S, T, E extends Throwable> extends Converter<S, T, E>, Transformer<S, T, E> {
}