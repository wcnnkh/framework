package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.MapperRegistry;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public interface ObjectMapper extends MapperRegistry<Object, ConversionException>, MappingRegistry,
		ObjectAccessFactoryRegistry, MappingStrategyFactory {

	default boolean isEntity(TypeDescriptor source, ParameterDescriptor parameterDescriptor) {
		return isEntity(parameterDescriptor.getTypeDescriptor());
	}

	/**
	 * 判断是否是实体对象
	 * 
	 * @param type
	 * @return
	 */
	default boolean isEntity(TypeDescriptor source) {
		return (!Value.isBaseType(source.getType()) && !source.isArray() && source.getType() != Object.class
				&& ReflectionUtils.isInstance(source.getType()) && !source.isMap() && !source.isCollection())
				|| isMappingRegistred(source.getType());
	}

	default void transform(ObjectAccess sourceAccess, @Nullable MappingContext sourceContext, Object target,
			TypeDescriptor targetType, @Nullable MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			ObjectAccess targetAccess = getObjectAccess(target, targetType);
			transform(sourceAccess, sourceContext, targetAccess, targetContext, mappingStrategy);
		} else {
			Mapping<? extends Member> targetMapping = getMapping(targetType.getType());
			transform(sourceAccess, sourceContext, target, targetType, targetContext, targetMapping, mappingStrategy);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, @Nullable MappingContext sourceContext,
			ObjectAccess targetAccess, @Nullable MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			ObjectAccess sourceAccess = getObjectAccess(source, sourceType);
			transform(sourceAccess, sourceContext, targetAccess, targetContext, mappingStrategy);
		} else {
			Mapping<? extends Member> sourceMapping = getMapping(sourceType.getType());
			transform(source, sourceType, sourceContext, sourceMapping, targetAccess, targetContext, mappingStrategy);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, @Nullable MappingContext sourceContext,
			Object target, TypeDescriptor targetType, @Nullable MappingContext targetContext,
			MappingStrategy mappingStrategy) throws MappingException {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			ObjectAccess sourceAccess = getObjectAccess(source, sourceType);
			transform(sourceAccess, sourceContext, target, targetType, targetContext, mappingStrategy);
		} else if (isObjectAccessFactoryRegistred(targetType.getType())) {
			ObjectAccess targetAccess = getObjectAccess(target, targetType);
			transform(source, sourceType, sourceContext, targetAccess, targetContext, mappingStrategy);
		} else {
			Mapping<? extends Member> sourceMapping = getMapping(sourceType.getType());
			Mapping<? extends Member> targetMapping = getMapping(targetType.getType());
			transform(source, sourceType, sourceContext, sourceMapping, target, targetType, targetContext,
					targetMapping, mappingStrategy);
		}
	}

	default <S extends Member, T extends Member> void transform(Object source, TypeDescriptor sourceType,
			@Nullable MappingContext sourceContext, Mapping<? extends S> sourceMapping, Object target,
			TypeDescriptor targetType, @Nullable MappingContext targetContext, Mapping<? extends T> targetMapping,
			MappingStrategy strategy) throws MappingException {
		for (Member targetField : targetMapping.getElements()) {
			if (targetField.isSupportSetter()) {
				strategy.transform(this, source, sourceType, sourceContext, sourceMapping, target, targetType,
						targetContext, targetMapping, targetField);
			}
		}
	}

	default <T extends Member> void transform(ObjectAccess sourceAccess, @Nullable MappingContext sourceContext,
			Object target, TypeDescriptor targetType, @Nullable MappingContext targetContext,
			Mapping<? extends T> targetMapping, MappingStrategy strategy) throws MappingException {
		for (T targetField : targetMapping.getElements()) {
			if (targetField.isSupportSetter()) {
				strategy.transform(this, sourceAccess, sourceContext, target, targetType, targetContext, targetMapping,
						targetField);
			}
		}
	}

	default <T extends Member> void transform(Object source, TypeDescriptor sourceType,
			@Nullable MappingContext sourceContext, Mapping<? extends T> sourceMapping, ObjectAccess targetAccess,
			@Nullable MappingContext targetContext, MappingStrategy strategy) throws MappingException {
		for (Member sourceField : sourceMapping.getElements()) {
			if (sourceField.isSupportGetter()) {
				strategy.transform(this, source, sourceType, sourceContext, sourceMapping, sourceField, targetAccess,
						targetContext);
			}
		}
	}

	default void transform(ObjectAccess sourceAccess, @Nullable MappingContext sourceContext, ObjectAccess targetAccess,
			@Nullable MappingContext targetContext, MappingStrategy strategy) throws MappingException {
		for (String name : sourceAccess.keys()) {
			strategy.transform(this, sourceAccess, sourceContext, name, targetAccess, targetContext);
		}
	}
}
