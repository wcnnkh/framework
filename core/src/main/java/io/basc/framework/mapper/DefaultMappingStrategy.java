package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.support.SetFilter;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.PredicateRegistry;
import io.basc.framework.util.Services;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.AliasFactory;
import io.basc.framework.value.Value;
import lombok.Data;

@Data
public class DefaultMappingStrategy implements MappingStrategy {
	private Services<SetFilter> filters = new Services<>();
	private AliasFactory aliasFactory;

	public DefaultMappingStrategy() {
		this(null);
	}

	private Elements<String> getAliasNames(Elements<String> names) {
		Elements<String> aliasNames = aliasFactory == null ? names : names.flatMap((name) -> {
			String[] aliasArray = aliasFactory.getAliases(name);
			if (aliasArray == null || aliasArray.length == 0) {
				return Elements.singleton(name);
			}

			return Elements.forArray(aliasArray).concat(Elements.singleton(name));
		});

		return parentMappingStrategy == null ? aliasNames : parentMappingStrategy.getAliasNames(aliasNames);
	}

	private void appendMapProperty(Map<String, Object> valueMap, String prefix, ObjectAccess sourceAccess,
			String nameConnector) {
		for (String key : sourceAccess.keys()) {
			if (!testSourceName(key)) {
				continue;
			}

			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Parameter parameter = sourceAccess.get(key);
				if (parameter == null) {
					continue;
				}

				if (isIgnoreNull() && !parameter.isPresent()) {
					continue;
				}

				parameter.setConverterIfAbsent(getConversionService());
				valueMap.put(
						StringUtils.isEmpty(prefix) ? key
								: key.substring(prefix.length()
										+ (prefix.endsWith(nameConnector) ? 0 : nameConnector.length())),
						parameter.get());
			}
		}
	}

	public ConversionService getConversionService() {
		if (conversionService != null) {
			return conversionService;
		}

		if (parentMappingStrategy != null) {
			return parentMappingStrategy.getConversionService();
		}

		return Sys.getEnv().getConversionService();
	}

	public String getSourceNamePrefix() {
		if (sourceNamePrefix != null) {
			return sourceNamePrefix;
		}
		return parentMappingStrategy == null ? null : parentMappingStrategy.getSourceNamePrefix();
	}

	public boolean isIgnoreNull() {
		if (ignoreNull != null) {
			return ignoreNull;
		}

		return parentMappingStrategy == null ? false : parentMappingStrategy.isIgnoreNull();
	}

	private boolean set(ObjectAccess<? extends E> targetAccess, Parameter targetValue) throws E {
		if (targetValue == null) {
			return false;
		}

		if (isIgnoreNull() && !targetValue.isPresent()) {
			// 如果忽略空，但目标为空就忽略
			return false;
		}

		targetValue.setConverterIfAbsent(getConversionService());
		targetAccess.set(targetValue);
		return true;
	}

	private Setter set(Value target, Field targetField, Parameter targetValue) {
		if (targetValue == null) {
			return null;
		}

		if (isIgnoreNull() && !targetValue.isPresent()) {
			// 如果忽略空，但目标为空就忽略
			return null;
		}

		targetValue.setConverterIfAbsent(getConversionService());
		return targetField.set(target, targetValue);
	}

	private boolean testSourceField(Field field) {
		if (!field.isSupportGetter()) {
			return false;
		}

		if (!field.getGetters().anyMatch((e) -> testSourceName(e.getName()))) {
			return false;
		}

		if (!predicateRegistry.test(field)) {
			return false;
		}

		return parentMappingStrategy == null ? true : parentMappingStrategy.testSourceField(field);
	}

	private boolean testSourceName(String name) {
		if (sourceNamePrefix == null) {
			return true;
		}

		if (!name.startsWith(sourceNamePrefix)) {
			return false;
		}
		return parentMappingStrategy == null ? true : parentMappingStrategy.testSourceName(name);
	}

	private boolean testTargetField(Field field) {
		if (!field.isSupportSetter()) {
			return false;
		}

		if (!predicateRegistry.test(field)) {
			return false;
		}

		return parentMappingStrategy == null ? true : parentMappingStrategy.testTargetField(field);
	}

	@Override
	public <S extends Field, T extends Field> void transform(ObjectMapper mapper, Object source,
			TypeDescriptor sourceType, MappingContext<? extends S> sourceContext, Mapping<? extends S> sourceMapping,
			Object target, TypeDescriptor targetType, MappingContext<? extends T> targetContext,
			Mapping<? extends T> targetMapping) throws MappingException {
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		for (Field targetField : targetMapping.getElements()) {

			if (!testTargetField(targetField)) {
				continue;
			}

			Elements<String> setterNames = getAliasNames(targetField.getAliasNames());
			for (String setterName : setterNames) {
				Elements<? extends Field> sourceFields = sourceMapping.getElements(setterName);
				for (Field sourceField : sourceFields) {
					for (Getter getter : sourceField.getGetters()) {

					}
				}
			}
		}
	}

	@Override
	public <S extends Field, T extends Field> void transform(ObjectMapper mapper, ObjectAccess sourceAccess,
			MappingContext<? extends S> sourceContext, Object target, TypeDescriptor targetType,
			MappingContext<? extends T> targetContext, Mapping<? extends T> targetMapping) throws MappingException {
		Value targetInstance = Value.of(target, targetType);
		// 不选择将elements转为map的原因是可能存在多相相同的field name
		for (Field field : targetMapping.getElements()) {
			if (!testTargetField(field)) {
				continue;
			}

			for (Setter setter : field.getSetters()) {
				if (mapper.isEntity(setter.getTypeDescriptor())) {
					// 发现是一个实体对象，进行解析
					Object entity = mapper.newInstance(setter.getTypeDescriptor());
					mapper.transform(sourceAccess, sourceContext, sourceAccess);
				}
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

	public void transform(ObjectMapper mapper, Object source, TypeDescriptor sourceType,
			Mapping<? extends Field> sourceMapping, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws MappingException {
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		for (Field targetField : targetMapping.getElements()) {
			if (!testTargetField(targetField)) {
				continue;
			}

			Elements<String> setterNames = getAliasNames(targetField.getAliasNames());
			for (String setterName : setterNames) {
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
}
