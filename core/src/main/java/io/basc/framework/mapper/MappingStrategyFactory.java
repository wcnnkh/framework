package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 映射策略工厂
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface MappingStrategyFactory<E extends Throwable> extends MappingStrategy<E> {
	/**
	 * 获取映射策略
	 * 
	 * @param typeDescriptor
	 * @return
	 */
	MappingStrategy<E> getMappingStrategy(TypeDescriptor typeDescriptor);

	@Override
	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E {
		getMappingStrategy(targetType).transform(source, sourceType, sourceMapping, target, targetType, targetMapping);
	}

	@Override
	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess<? extends E> targetAccess) throws E {
		getMappingStrategy(targetAccess.getTypeDescriptor()).transform(source, sourceType, sourceMapping, targetAccess);
	}

	@Override
	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		getMappingStrategy(targetType).transform(sourceAccess, target, targetType, targetMapping);
	}

	@Override
	default void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E {
		getMappingStrategy(targetAccess.getTypeDescriptor()).transform(sourceAccess, targetAccess);
	}
}
