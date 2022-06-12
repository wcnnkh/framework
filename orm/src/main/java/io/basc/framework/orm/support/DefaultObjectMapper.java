package io.basc.framework.orm.support;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.ObjectAccessFactoryRegistry;
import io.basc.framework.orm.repository.AbstractRepositoryObjectMapper;
import io.basc.framework.util.ClassUtils;

public class DefaultObjectMapper<E extends Throwable> extends AbstractRepositoryObjectMapper<Object, E>
		implements ObjectAccessFactoryRegistry<E> {
	private final Map<Class<?>, ObjectAccessFactory<?, ? extends E>> map = new TreeMap<>(
			(o1, o2) -> o1 == o2 ? 0 : (ClassUtils.isAssignable(o1, o2) ? 1 : -1));

	@Override
	public boolean isObjectAccessFactoryRegistred(Class<?> type) {
		if (map.containsKey(type)) {
			return true;
		}

		for (Class<?> key : map.keySet()) {
			if (ClassUtils.isAssignable(key, type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <S> void registerObjectAccessFactory(Class<S> type, ObjectAccessFactory<? super S, ? extends E> factory) {
		map.put(type, factory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ObjectAccessFactory<S, E> getObjectAccessFactory(Class<? extends S> type) {
		Object object = map.get(type);
		if (object == null) {
			for (Entry<Class<?>, ObjectAccessFactory<?, ? extends E>> entry : map.entrySet()) {
				if (ClassUtils.isAssignable(entry.getKey(), type)) {
					object = entry.getValue();
					break;
				}
			}
		}
		return (ObjectAccessFactory<S, E>) object;
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (!isTransformerRegistred(targetType.getType()) && isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, getObjectAccess(target, targetType));
			return;
		}
		super.transform(source, sourceType, target, targetType);
	}

	@Override
	public void reverseTransform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E {
		if (!isReverseTransformerRegistred(sourceType.getType())
				&& isObjectAccessFactoryRegistred(sourceType.getType())) {
			reverseTransform(getObjectAccess(source, sourceType), target, targetType);
			return;
		}
		super.reverseTransform(source, sourceType, target, targetType);
	}
}
