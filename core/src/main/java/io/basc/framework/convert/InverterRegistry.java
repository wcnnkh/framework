package io.basc.framework.convert;

public interface InverterRegistry<S, E extends Throwable> extends Inverter<S, Object, E> {
	default boolean isInverterRegistred(Class<?> type) {
		return getInverter(type) != null;
	}

	<T> Inverter<S, T, E> getInverter(Class<? extends T> type);

	<T> void registerInverter(Class<T> type, Inverter<? extends S, ? super T, ? extends E> inverter);

	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		Inverter<S, Object, E> inverter = getInverter(targetType.getType());
		if (inverter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return inverter.invert(source, sourceType, targetType);
	}
}
