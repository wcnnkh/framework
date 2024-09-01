package io.basc.framework.convert.factory.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.factory.config.ConverterRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.register.StandardRegistration;
import io.basc.framework.util.register.Registration;

public class DefaultConverterFactory<S, E extends Throwable> implements ConverterRegistry<S, E> {
	private TreeMap<Class<?>, Converter<? super S, ? extends Object, ? extends E>> converterMap;

	protected <T> T get(Class<?> type, TreeMap<Class<?>, T> sourceMap) {
		if (sourceMap == null || sourceMap.isEmpty()) {
			return null;
		}

		T value = sourceMap.get(type);
		if (value != null) {
			return value;
		}

		for (Entry<Class<?>, T> entry : sourceMap.entrySet()) {
			if (type.isAssignableFrom(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return (Converter<S, T, E>) get(requiredType, converterMap);
	}

	protected <T> TreeMap<Class<?>, T> register(Class<?> type, T conversion, TreeMap<Class<?>, T> sourceMap) {
		Assert.requiredArgument(type != null, "type");
		if (conversion == null) {
			if (sourceMap != null) {
				sourceMap.remove(type);
			}
		} else {
			if (sourceMap == null) {
				sourceMap = new TreeMap<>(TypeComparator.DEFAULT);
			}

			sourceMap.put(type, conversion);
		}
		return sourceMap;
	}

	@Override
	public <T> Registration registerConverter(Class<T> targetType,
			Converter<? super S, ? extends T, ? extends E> converter) {
		this.converterMap = register(targetType, converter, converterMap);
		return StandardRegistration.of(() -> converterMap.remove(targetType));
	}
}
