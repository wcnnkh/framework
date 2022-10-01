package io.basc.framework.convert;

public interface InverterFactory<T, E extends Throwable>
		extends ReverseTransformerFactory<T, E>, Inverter<Object, T, E> {
	boolean isInverterRegistred(Class<?> type);

	<S> Inverter<S, T, E> getInverter(Class<? extends S> type);

	<S> void registerInverter(Class<S> type, Inverter<? extends S, ? extends T, ? extends E> inverter);

	@SuppressWarnings("unchecked")
	@Override
	default T invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (T) source;
		}

		Inverter<Object, T, E> inverter = getInverter(targetType.getType());
		if (inverter == null) {
			T target = (T) newInstance(targetType);
			if (target == null) {
				return null;
			}
			reverseTransform(source, sourceType, target, targetType);
			return target;
		}
		return inverter.invert(source, sourceType, targetType);
	}
}
