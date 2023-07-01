package io.basc.framework.convert;

public interface ReversibleConverter<S, T, E extends Throwable> extends Converter<S, T, E>, Inverter<S, T, E> {
}