package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.PredicateRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;
import lombok.Setter;

@Setter
public class DefaultMappingStrategy<E extends Throwable> implements MappingStrategy<E> {
	private static Logger logger = LoggerFactory.getLogger(DefaultMappingStrategy.class);
	private String sourceNamePrefix;
	private String nameConnector;
	private ConversionService conversionService;
	private final PredicateRegistry<Field> predicateRegistry = new PredicateRegistry<>();
	private final DefaultMappingStrategy<E> parentMappingStrategy;

	public String getSourceNamePrefix() {
		if (sourceNamePrefix != null) {
			return sourceNamePrefix;
		}
		return parentMappingStrategy == null ? null : parentMappingStrategy.getSourceNamePrefix();
	}

	private boolean testSourceField(Field field) {
		if (sourceNamePrefix == null) {
			return true;
		}

		if (!field.getName().startsWith(sourceNamePrefix)) {
			return false;
		}

		if (!predicateRegistry.test(field)) {
			return false;
		}

		return parentMappingStrategy == null ? true : parentMappingStrategy.testSourceField(field);
	}

	private boolean testTargetField(Field field) {
		if (!predicateRegistry.test(field)) {
			return false;
		}

		return parentMappingStrategy == null ? true : parentMappingStrategy.testTargetField(field);
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

	public DefaultMappingStrategy() {
		this(null);
	}

	public DefaultMappingStrategy(@Nullable DefaultMappingStrategy<E> parentMappingStrategy) {
		this.parentMappingStrategy = parentMappingStrategy;
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

	public void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E {
		List<? extends Field> sourceFields = sourceMapping.getElements().toList();
		List<? extends Field> targetFields = targetMapping.getElements().toList();
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		// source元素是否比right元素多
		boolean sourceGtRight = sourceFields.size() > targetFields.size();
		ConversionService conversionService = getConversionService();
		// 用少的元素做迭代
		for (Field f1 : sourceGtRight ? targetFields : sourceFields) {
			Iterator<? extends Field> iterator = sourceGtRight ? sourceFields.iterator() : targetFields.iterator();
			while (iterator.hasNext()) {
				Field f2 = iterator.next();
				Field sourceField;
				Field targetField;
				if (sourceGtRight) {
					targetField = f1;
					sourceField = f2;
				} else {
					targetField = f2;
					sourceField = f1;
				}

				if (!testSourceField(sourceField) || !testTargetField(targetField)) {
					continue;
				}

				Parameter sourceValue = sourceField.get(sourceInstance);
				if (sourceValue == null) {
					continue;
				}

				sourceValue.setConverter(conversionService);
				targetField.set(targetInstance, sourceValue);
			}
		}
	}

	public void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess<? extends E> targetAccess) throws E {
		Value sourceInstance = Value.of(source, sourceType);
		for (Field field : sourceMapping.getElements()) {
			Parameter parameter = field.get(sourceInstance);
			if (parameter == null) {
				continue;
			}

			targetAccess.set(parameter);
		}
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		Value targetInstance = Value.of(target, targetType);
		ConversionService conversionService = getConversionService();
		for (Field field : targetMapping.getElements()) {
			if (!testTargetField(field)) {
				continue;
			}

			// TODO 应该以插入为准
			Parameter parameter = sourceAccess.get(field.getName());
			if (parameter == null) {
				continue;
			}

			parameter.setConverter(conversionService);
			field.set(targetInstance, parameter);
		}
	}

	public void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E {
		sourceAccess.copy(targetAccess);
	}

	protected void appendMapProperty(Map<String, Object> valueMap, String prefix, ObjectAccess<E> objectAccess,
			ObjectMapperContext context) throws E {
		for (String key : objectAccess.keys()) {
			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Value value = objectAccess.get(key);
				if (value == null) {
					continue;
				}

				if (context.isIgnoreNull() && !value.isPresent()) {
					continue;
				}

				valueMap.put(
						StringUtils.isEmpty(prefix) ? key
								: key.substring(prefix.length()
										+ (prefix.endsWith(getNameConnector()) ? 0 : getNameConnector().length())),
						value.get());
			}
		}
	}
}
