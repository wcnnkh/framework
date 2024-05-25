package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ReversibleMapper;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.access.ObjectAccess;
import io.basc.framework.mapper.access.ObjectAccessFactoryRegistry;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.MappingContext;
import io.basc.framework.mapper.entity.MappingException;
import io.basc.framework.mapper.entity.MappingStrategy;
import io.basc.framework.mapper.entity.MappingStrategyFactory;
import io.basc.framework.mapper.entity.factory.config.MappingRegistry;
import io.basc.framework.value.ParameterDescriptor;

public interface ObjectMapper extends ReversibleMapper<Object, Object, ConversionException>, MappingRegistry,
		ObjectAccessFactoryRegistry, MappingStrategyFactory, ConversionService {

	default boolean isEntity(TypeDescriptor source, ParameterDescriptor parameterDescriptor) {
		return isEntity(parameterDescriptor.getTypeDescriptor());
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			MappingStrategy mappingStrategy) {
		transform(source, sourceType, null, target, targetType, null, mappingStrategy);
	}

	default void transform(Object source, Object target, MappingStrategy mappingStrategy) {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target), mappingStrategy);
	}

	default void transform(ObjectAccess sourceAccess, @Nullable MappingContext sourceContext, Object target,
			TypeDescriptor targetType, @Nullable MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			ObjectAccess targetAccess = getObjectAccess(target, targetType);
			transform(sourceAccess, sourceContext, targetAccess, targetContext, mappingStrategy);
		} else {
			Mapping<? extends FieldDescriptor> targetMapping = getMapping(targetType.getType());
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
			Mapping<? extends FieldDescriptor> sourceMapping = getMapping(sourceType.getType());
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
			Mapping<? extends FieldDescriptor> sourceMapping = getMapping(sourceType.getType());
			Mapping<? extends FieldDescriptor> targetMapping = getMapping(targetType.getType());
			transform(source, sourceType, sourceContext, sourceMapping, target, targetType, targetContext,
					targetMapping, mappingStrategy);
		}
	}

	default <S extends FieldDescriptor, T extends FieldDescriptor> void transform(Object source,
			TypeDescriptor sourceType, @Nullable MappingContext sourceContext, Mapping<? extends S> sourceMapping,
			Object target, TypeDescriptor targetType, @Nullable MappingContext targetContext,
			Mapping<? extends T> targetMapping, MappingStrategy strategy) throws MappingException {
		for (FieldDescriptor targetField : targetMapping.getElements()) {
			if (targetField.isSupportSetter()) {
				strategy.transform(this, source, sourceType, sourceContext, sourceMapping, target, targetType,
						targetContext, targetMapping, targetField);
			}
		}
	}

	default <T extends FieldDescriptor> void transform(ObjectAccess sourceAccess,
			@Nullable MappingContext sourceContext, Object target, TypeDescriptor targetType,
			@Nullable MappingContext targetContext, Mapping<? extends T> targetMapping, MappingStrategy strategy)
			throws MappingException {
		for (T targetField : targetMapping.getElements()) {
			if (targetField.isSupportSetter()) {
				strategy.transform(this, sourceAccess, sourceContext, target, targetType, targetContext, targetMapping,
						targetField);
			}
		}
	}

	default <T extends FieldDescriptor> void transform(Object source, TypeDescriptor sourceType,
			@Nullable MappingContext sourceContext, Mapping<? extends T> sourceMapping, ObjectAccess targetAccess,
			@Nullable MappingContext targetContext, MappingStrategy strategy) throws MappingException {
		for (FieldDescriptor sourceField : sourceMapping.getElements()) {
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
