package io.basc.framework.convert.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;

public interface MappingFactory<I extends KeyValue<K, V>, K, V extends Accessor, E extends Throwable> extends Transformer<T, T, E> {
	Mapping<K, V> getMapping(T transform, TypeDescriptor typeDescriptor);

	@Override
	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		Mapping<K, V> sourceMapping = getMapping(source, sourceType);
		Mapping<K, V> targetMapping = getMapping(target, targetType);
		for (KeyValue<K, V> sourceKeyValue : sourceMapping) {
			Elements<V> accesstors = targetMapping.getElements(sourceKeyValue.getKey());
			for (V accesstor : accesstors) {
				if (accesstor.isReadOnly()) {
					continue;
				}

				// 匹配类型
				if (!sourceKeyValue.getValue().getTypeDescriptor()
						.isAssignableTo(accesstor.getRequiredTypeDescriptor())) {
					continue;
				}
				accesstor.set(sourceKeyValue.getValue());
			}
		}
	}
}
