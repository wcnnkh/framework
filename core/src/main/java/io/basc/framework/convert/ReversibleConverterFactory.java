package io.basc.framework.convert;

public interface ReversibleConverterFactory<S, E extends Throwable>
		extends InverterFactory<S, E>, ConverterFactory<S, E>, ReversibleConverter<S, Object, E> {

	boolean isReversibleConverterRegistred(Class<?> type);

	<T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type);

	<T> void registerReversibleConverter(Class<T> type, ReversibleConverter<S, ? extends T, ? extends E> converter);
	
	@Override
	default Object newInstance(TypeDescriptor type) {
		return ConverterFactory.super.newInstance(type);
	}
}
