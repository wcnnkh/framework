package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.PredicateRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.AliasFactory;
import io.basc.framework.value.Value;
import lombok.Data;

@Data
public class DefaultMappingStrategy<E extends Throwable> implements MappingStrategy<E> {
	private String sourceNamePrefix;
	private String nameConnector = ".";
	private ConversionService conversionService;
	private final PredicateRegistry<Field> predicateRegistry = new PredicateRegistry<>();
	private final DefaultMappingStrategy<E> parentMappingStrategy;
	private Boolean ignoreNull;
	private AliasFactory aliasFactory;

	public DefaultMappingStrategy() {
		this(null);
	}

	public DefaultMappingStrategy(@Nullable DefaultMappingStrategy<E> parentMappingStrategy) {
		this.parentMappingStrategy = parentMappingStrategy;
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

	private void appendMapProperty(Map<String, Object> valueMap, String prefix, ObjectAccess<E> sourceAccess,
			String nameConnector) throws E {
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

		if (!testSourceName(field.getName())) {
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

	public void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E {
		List<? extends Field> targetFields = targetMapping.getElements().toList();
		if (targetFields.isEmpty()) {
			return;
		}

		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		for (Field sourceField : sourceMapping.getElements()) {
			if (!testSourceField(sourceField)) {
				continue;
			}

			Iterator<? extends Field> iterator = targetFields.iterator();
			while (iterator.hasNext()) {
				Field targetField = iterator.next();
				if (!testTargetField(targetField)) {
					iterator.remove();
					continue;
				}

				Elements<String> setterNames = getAliasNames(targetField.getSetters().map((e) -> e.getName()));
				if (!setterNames.contains(sourceField.getName())) {
					continue;
				}

				Parameter sourceValue = sourceField.get(sourceInstance);
				if (sourceValue == null) {
					continue;
				}

				if (set(targetInstance, targetField, sourceValue) != null) {
					iterator.remove();
				}
			}
		}
	}

	public void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess<? extends E> targetAccess) throws E {
		Value sourceInstance = Value.of(source, sourceType);
		for (Field field : sourceMapping.getElements()) {
			if (!testSourceField(field)) {
				continue;
			}

			Parameter parameter = field.get(sourceInstance);
			if (parameter == null) {
				continue;
			}

			set(targetAccess, parameter);
		}
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
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
				for (Setter setter : field.getSetters()) {
					// 如果是map类型
					if (setter.getTypeDescriptor().isMap()) {
						Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
						for (String name : getAliasNames(Elements.singleton(field.getName()))) {
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

	public void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E {
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
