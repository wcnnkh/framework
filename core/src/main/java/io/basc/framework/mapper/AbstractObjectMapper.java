package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.AliasRegistry;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public abstract class AbstractObjectMapper<S, E extends Throwable> extends SimpleReverseMapperFactory<S, E>
		implements ObjectMapper<S, E>, ConversionServiceAware, ObjectAccessFactory<S, E> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<Class<?>, Structure<? extends Field>> map = new ConcurrentHashMap<>();
	private String namePrefix;
	private String nameConnector = ".";
	private boolean transformSuperclass = true;
	private ConversionService conversionService;
	private Level loggerLevel = io.basc.framework.logger.Levels.DEBUG.getValue();
	private Predicate<Field> filter;
	private Field parentField;
	private AliasRegistry aliasRegistry;

	/**
	 * 名称嵌套解析
	 */
	private boolean nameNesting = true;

	public final ConversionService getConversionService() {
		if (this.conversionService != null) {
			return this.conversionService;
		}
		return Sys.env.getConversionService();
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public final String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public final String getNameConnector() {
		return nameConnector;
	}

	public void setNameConnector(String nameConnector) {
		this.nameConnector = nameConnector;
	}

	public final boolean isNameNesting() {
		return nameNesting;
	}

	public void setNameNesting(boolean nameNesting) {
		this.nameNesting = nameNesting;
	}

	public final boolean isTransformSuperclass() {
		return transformSuperclass;
	}

	public void setTransformSuperclass(boolean transformSuperclass) {
		this.transformSuperclass = transformSuperclass;
	}

	@Override
	public Structure<? extends Field> getStructure(Class<?> entityClass) {
		Structure<? extends Field> structure = map.get(entityClass);
		if (structure == null) {
			structure = ObjectMapper.super.getStructure(entityClass);
		}

		if (structure.getParent() == null && this.parentField != null) {
			structure = structure.setParentField(this.parentField);
		}

		if (transformSuperclass) {
			structure = structure.all();
		}

		if (filter != null) {
			structure = structure.filter(filter);
		}
		return structure.shared();
	}

	public final Level getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(Level loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	public final Predicate<Field> getFilter() {
		return filter;
	}

	public final void addFilter(Predicate<Field> filter) {
		if (filter == null) {
			return;
		}

		if (this.filter == null) {
			this.filter = filter;
		} else {
			this.filter = this.filter.and(filter);
		}
	}

	public void setFilter(Predicate<Field> filter) {
		this.filter = filter;
	}

	public final Field getParentField() {
		return parentField;
	}

	public void setParentField(Field parentField) {
		this.parentField = parentField;
	}

	public final Logger getLogger() {
		return logger;
	}

	@Override
	public void registerStructure(Class<?> entityClass, Structure<? extends Field> structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (structure == null) {
			map.remove(entityClass);
		} else {
			map.put(entityClass, structure);
		}
	}

	public AliasRegistry getAliasRegistry() {
		return aliasRegistry;
	}

	public void setAliasRegistry(AliasRegistry aliasRegistry) {
		this.aliasRegistry = aliasRegistry;
	}

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return map.containsKey(entityClass);
	}

	public Collection<String> getNames(Field field) {
		return field.getNames(nameNesting, getAliasRegistry(), namePrefix, nameConnector);
	}

	@Override
	public Processor<Field, Parameter, E> getValueProcessor(S source, TypeDescriptor sourceType) throws E {
		ObjectAccess<E> objectAccess = getObjectAccess(source, sourceType);
		return (p) -> {
			Collection<String> names = getNames(p);
			if (logger.isTraceEnabled()) {
				logger.trace(p.getName() + " - " + names);
			}

			for (String name : names) {
				Parameter value = objectAccess.get(name);
				if (value == null || value.isNull()) {
					continue;
				}
				return value;
			}

			if (Map.class.isAssignableFrom(p.getSetter().getType())) {
				Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
				for (String name : names) {
					appendMapProperty(valueMap, source, sourceType, name + nameConnector, objectAccess);
				}
				if (!CollectionUtils.isEmpty(valueMap)) {
					Value value = new AnyValue(valueMap,
							TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class));
					return new Parameter(namePrefix == null ? p.getName() : (namePrefix + p.getName()), value);
				}
			}
			return null;
		};
	}

	protected void appendMapProperty(Map<String, Object> valueMap, S source, TypeDescriptor sourceType, String prefix,
			ObjectAccess<E> objectAccess) throws E {
		Enumeration<String> keys = objectAccess.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Value value = objectAccess.get(key);
				if (value == null) {
					continue;
				}

				valueMap.put(
						StringUtils
								.isEmpty(prefix)
										? key
										: key.substring(prefix.length()
												+ (prefix.endsWith(nameConnector) ? 0 : nameConnector.length())),
						value.get());
			}
		}
	}

	@Override
	public void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Field targetField, Value sourceValue) {
		if (sourceValue == null) {
			return;
		}

		TypeDescriptor targetValueType = new TypeDescriptor(targetField.getSetter());
		Object value = getConversionService().convert(sourceValue.get(), sourceValue.getTypeDescriptor(),
				targetValueType);
		if (logger.isLoggable(loggerLevel)) {
			logger.log(loggerLevel, "Property {} on target {} set value {}", targetField.getSetter().getName(), target,
					value);
		}
		targetField.set(target, value);
	}

	@Override
	public void transform(S source, TypeDescriptor sourceType, ObjectAccess<? extends E> targetAccess) throws E {
		transform(source, sourceType, targetAccess, (key) -> {
			if (namePrefix == null || key.startsWith(namePrefix)) {
				return key.substring(namePrefix.length());
			}
			return null;
		});
	}

	@Override
	public void reverseTransform(ObjectAccess<? extends E> sourceAccess, S target, TypeDescriptor targetType) throws E {
		reverseTransform(sourceAccess, target, targetType, (key) -> {
			if (namePrefix == null || key.startsWith(namePrefix)) {
				return key.substring(namePrefix.length());
			}
			return null;
		});
	}
}
