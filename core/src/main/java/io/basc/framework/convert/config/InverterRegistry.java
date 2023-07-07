package io.basc.framework.convert.config;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.Inverter;
import io.basc.framework.convert.TypeDescriptor;

public interface InverterRegistry<S, E extends Throwable> extends Inverter<S, Object, E> {
	default boolean isInverterRegistred(Class<?> sourceType) {
		return getInverter(sourceType) != null;
	}

	<T> Inverter<S, T, E> getInverter(Class<? extends T> sourceType);

	<T> void registerInverter(Class<T> sourceType, Inverter<? extends S, ? super T, ? extends E> inverter);

	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		Inverter<S, Object, E> inverter = getInverter(sourceType.getType());
		if (inverter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return inverter.invert(source, sourceType, targetType);
	}
}
