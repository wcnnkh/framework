package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 映射策略
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface MappingStrategy<E extends Throwable> {

	void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping, Object target,
			TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E;

	void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess<? extends E> targetAccess) throws E;

	void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E;

	void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E;
}
