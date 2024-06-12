package io.basc.framework.transform.factory.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.transform.Transformer;
import io.basc.framework.transform.factory.config.TransformerRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.DisposableRegistration;
import io.basc.framework.util.Registration;
import io.basc.framework.util.comparator.TypeComparator;

public class DefaultTransformerFactory<S, E extends Throwable> implements TransformerRegistry<S, E> {
	private TreeMap<Class<?>, Transformer<? super S, ?, ? extends E>> transformerMap;

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
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> requiredType) {
		return (Transformer<S, T, E>) get(requiredType, transformerMap);
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
	public <T> Registration registerTransformer(Class<T> targetType,
			Transformer<? super S, ? extends T, ? extends E> converter) {
		this.transformerMap = register(targetType, converter, transformerMap);
		return DisposableRegistration.of(() -> transformerMap.remove(targetType));
	}
}
