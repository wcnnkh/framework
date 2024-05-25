package io.basc.framework.convert.factory.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.factory.config.ConverterRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.DisposableRegistration;
import io.basc.framework.util.Registration;
import io.basc.framework.util.comparator.TypeComparator;

public class DefaultConverterFactory<S, E extends Throwable, C extends Converter<? super S, ? extends Object, ? extends E>>
		implements ConverterRegistry<S, E, C> {
	private TreeMap<Class<?>, C> converterMap;

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
	public Registration registerConverter(Class<?> type, C converter) {
		this.converterMap = register(type, converter, converterMap);
		return DisposableRegistration.of(() -> converterMap.remove(type));
	}
}
