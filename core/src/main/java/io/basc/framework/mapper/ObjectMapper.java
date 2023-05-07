package io.basc.framework.mapper;

import java.util.Iterator;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ReversibleMapperFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;

public interface ObjectMapper extends ReversibleMapperFactory<Object, ConversionException>, MappingFactory,
		ObjectAccessFactoryRegistry, MappingStrategyFactory {

	/**
	 * 获取映射策略
	 * 
	 * @param typeDescriptor
	 * @return
	 */
	MappingStrategy getMappingStrategy(TypeDescriptor typeDescriptor);

	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws MappingException {
		return convert(source, sourceType, targetType, getMappingStrategy(targetType));
	}

	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType, MappingContext context,
			MappingStrategy mappingStrategy) throws MappingException {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		if (isConverterRegistred(targetType.getType())) {
			return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
		}

		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, target, targetType, mappingStrategy);
		return target;
	}

	@Override
	default Object invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws MappingException {
		return invert(source, sourceType, targetType, getMappingStrategy(targetType));
	}

	default Object invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType,
			MappingContext<? extends Field> context, MappingStrategy mappingStrategy) throws MappingException {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		if (isInverterRegistred(sourceType.getType())) {
			return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
		}

		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}

		reverseTransform(source, sourceType, target, targetType, mappingStrategy);
		return target;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws MappingException {
		reverseTransform(source, sourceType, target, targetType, getMappingStrategy(targetType));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			MappingContext<? extends Field> context, MappingStrategy mappingStrategy) throws MappingException {
		if (isReverseTransformerRegistred(sourceType.getType())) {
			ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, getObjectAccess(target, targetType), mappingStrategy);
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, mappingStrategy);
			return;
		}

		mappingStrategy.transform(this, target, targetType, getMapping(targetType.getType()), source, sourceType,
				getMapping(sourceType.getType()));
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, MappingContext<? extends Field> context,
			MappingStrategy mappingStrategy) throws MappingException {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			mappingStrategy.transform(this, source, sourceType, sourceMapping, getObjectAccess(target, targetType));
		} else {
			mappingStrategy.transform(this, source, sourceType, getMapping(targetType.getType()), target, targetType,
					getMapping(targetType.getType()));
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping, MappingContext<? extends Field> context,
			MappingStrategy mappingStrategy) throws MappingException {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			mappingStrategy.transform(this, getObjectAccess(source, sourceType), target, targetType, targetMapping);
		} else {
			mappingStrategy.transform(this, source, sourceType, getMapping(sourceType.getType()), target, targetType,
					targetMapping);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, ObjectAccess targetAccess,
			MappingContext<? extends Field> context, MappingStrategy mappingStrategy) throws MappingException {
		if (source == null) {
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			mappingStrategy.transform(this, getObjectAccess(source, sourceType), targetAccess);
			return;
		}

		mappingStrategy.transform(this, source, sourceType, getMapping(sourceType.getType()), targetAccess);
	}

	default void transform(ObjectAccess sourceAccess, Object target, TypeDescriptor targetType,
			@Nullable MappingContext<? extends Field> context, MappingStrategy mappingStrategy)
			throws MappingException {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			mappingStrategy.transform(this, sourceAccess, getObjectAccess(target, targetType));
			return;
		}
		mappingStrategy.transform(this, sourceAccess, target, targetType, getMapping(targetType.getType()));
	}

	@Override
	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws MappingException, ConverterNotFoundException {
		transform(source, sourceType, target, targetType, getMappingStrategy(targetType));
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			MappingContext<? extends Field> context, MappingStrategy mappingStrategy)
			throws MappingException, ConverterNotFoundException {
		if (isTransformerRegistred(targetType.getType())) {
			ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, mappingStrategy);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform((Object) source, sourceType, getObjectAccess(target, targetType), mappingStrategy);
			return;
		}

		mappingStrategy.transform(this, source, sourceType, getMapping(sourceType.getType()), target, targetType,
				getMapping(targetType.getType()));
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws MappingException {
		getMappingStrategy(targetType).transform(this, source, sourceType, sourceMapping, target, targetType,
				targetMapping);
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess targetAccess) throws MappingException {
		getMappingStrategy(targetAccess.getTypeDescriptor()).transform(this, source, sourceType, sourceMapping,
				targetAccess);
	}

	default void transform(ObjectAccess sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws MappingException {
		getMappingStrategy(targetType).transform(this, sourceAccess, target, targetType, targetMapping);
	}

	default void transform(ObjectAccess sourceAccess, ObjectAccess targetAccess) throws MappingException {
		getMappingStrategy(targetAccess.getTypeDescriptor()).transform(this, sourceAccess, targetAccess);
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws MappingException {
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		for (Field targetField : targetMapping.getElements()) {
			for (String setterName : targetField.getAliasNames()) {
				Elements<? extends Field> sourceFields = sourceMapping.getElements(setterName);
				for (Field sourceField : sourceFields) {
					for (Getter getter : sourceField.getGetters()) {

					}
				}
			}
		}

		for (Field sourceField : sourceMapping.getElements()) {
			if (!testSourceField(sourceField)) {
				continue;
			}

			Iterator<? extends Field> iterator = targetFields.iterator();
			Elements<String> getterNames = sourceField.getGetters().map((e) -> e.getName());
			while (iterator.hasNext()) {
				Field targetField = iterator.next();
				if (!testTargetField(targetField)) {
					iterator.remove();
					continue;
				}

				Elements<String> setterNames = getAliasNames(targetField.getSetters().map((e) -> e.getName()));
				if (!setterNames.anyMatch(getterNames, StringUtils::equals)) {
					continue;
				}

				Parameter sourceValue = sourceField.get(sourceInstance,
						targetField.getSetters().map((e) -> e.getTypeDescriptor().getResolvableType()));
				if (sourceValue == null) {
					continue;
				}

				if (set(targetInstance, targetField, sourceValue) != null) {
					iterator.remove();
				}
			}
		}
	}

	public void transform(ObjectMapper mapper, Object source, TypeDescriptor sourceType,
			Mapping<? extends Field> sourceMapping, ObjectAccess targetAccess) throws MappingException {
		Value sourceInstance = Value.of(source, sourceType);
		for (Field field : sourceMapping.getElements()) {
			if (!testSourceField(field)) {
				continue;
			}

			Parameter parameter = field.get(sourceInstance, null);
			if (parameter == null) {
				continue;
			}

			set(targetAccess, parameter);
		}
	}

	public void transform(ObjectMapper mapper, ObjectAccess sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws MappingException {
		Value targetInstance = Value.of(target, targetType);
		// 不选择将elements转为map的原因是可能存在多相相同的field name
		for (Field field : targetMapping.getElements()) {
			if (!testTargetField(field)) {
				continue;
			}

			Elements<String> setterName = getAliasNames(field.getSetters().map((e) -> e.getName()));
			Parameter parameter = null;
			for (String name : setterName) {
				parameter = sourceAccess.get(name);
				if (parameter != null) {
					break;
				}
			}

			if (parameter == null) {
				// 如果没有找到对应的值
				Elements<String> getterNames = getAliasNames(field.getGetters().map((e) -> e.getName()));
				for (Setter setter : field.getSetters()) {
					// 如果是map类型
					if (setter.getTypeDescriptor().isMap()) {
						Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
						for (String name : getterNames) {
							appendMapProperty(valueMap, name + nameConnector, sourceAccess, nameConnector);
						}
						if (!CollectionUtils.isEmpty(valueMap)) {
							Value value = Value.of(valueMap,
									TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class));
							parameter = new Parameter(setter.getName(), value);
							break;
						}
					}
				}
			}

			if (parameter == null) {
				continue;
			}

			set(targetInstance, field, parameter);
		}
	}

	public void transform(ObjectMapper mapper, ObjectAccess sourceAccess, ObjectAccess targetAccess)
			throws MappingException {
		for (String key : sourceAccess.keys()) {
			if (key == null) {
				continue;
			}

			if (!testSourceName(key)) {
				continue;
			}

			Parameter parameter = sourceAccess.get(key);
			if (parameter == null) {
				continue;
			}

			set(targetAccess, parameter);
		}
	}

	Elements<Field> expandEntity(Field entityField);
}
