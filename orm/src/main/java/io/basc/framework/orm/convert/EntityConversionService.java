package io.basc.framework.orm.convert;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.factory.support.UnsafeNoArgsInstanceFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Structure;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConfigurableAccept;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.AliasRegistry;

public abstract class EntityConversionService extends ConditionalConversionService {
	private static Logger logger = LoggerFactory.getLogger(EntityConversionService.class);
	private AliasRegistry aliasRegistry;
	private final ConfigurableAccept<Field> fieldAccept = new ConfigurableAccept<Field>();
	private String prefix;
	private String connector = ".";
	private boolean strict = false;
	private NoArgsInstanceFactory instanceFactory;
	private Level loggerLevel = io.basc.framework.logger.Levels.DEBUG.getValue();
	private ObjectRelationalMapper mapper;
	private Field parentField;
	// 是否先检查key存在
	private boolean checkKeyExists = false;
	// 默认是所有字段
	private boolean useSuperClass = true;

	public final boolean isCheckKeyExists() {
		return checkKeyExists;
	}

	public boolean isUseSuperClass() {
		return useSuperClass;
	}

	public void setUseSuperClass(boolean useSuperClass) {
		this.useSuperClass = useSuperClass;
	}

	public void setCheckKeyExists(boolean checkKeyExists) {
		this.checkKeyExists = checkKeyExists;
	}

	public final Field getParentField() {
		return parentField;
	}

	public void setParentField(Field parentField) {
		this.parentField = parentField;
	}

	public ObjectRelationalMapper getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(ObjectRelationalMapper mapper) {
		this.mapper = mapper;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory == null ? UnsafeNoArgsInstanceFactory.INSTANCE : instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	@Override
	public ConversionService getConversionService() {
		ConversionService conversionService = super.getConversionService();
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public Level getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(Level loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	public AliasRegistry getAliasRegistry() {
		return aliasRegistry;
	}

	public void setAliasRegistry(AliasRegistry aliasRegistry) {
		this.aliasRegistry = aliasRegistry;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public final String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public ConfigurableAccept<Field> getFieldAccept() {
		return fieldAccept;
	}

	public final String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	protected void appendMapProperty(Map<String, Object> valueMap, Object source, String prefix) {
		Enumeration<String> keys = keys(source);
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Object value = getProperty(source, key);
				if (value == null) {
					continue;
				}

				valueMap.put(
						StringUtils.isEmpty(prefix) ? key
								: key.substring(
										prefix.length() + (prefix.endsWith(connector) ? 0 : connector.length())),
						value);
			}
		}
	}

	@Nullable
	private Object getProperty(Object source, Property field) {
		Collection<String> names = getSetterNames(source.getClass(), field);
		if (logger.isTraceEnabled()) {
			logger.trace(field + " - " + names);
		}

		for (String name : names) {
			if (!checkKeyExists || containsKey(source, name)) {
				Object value = getProperty(source, name);
				if (value == null) {
					continue;
				}
				return value;
			}
		}

		if (Map.class.isAssignableFrom(field.getSetter().getType())) {
			Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
			for (String name : names) {
				appendMapProperty(valueMap, source, name + connector);
			}
			if (!CollectionUtils.isEmpty(valueMap)) {
				return valueMap;
			}
		}
		return null;
	}

	protected abstract Enumeration<String> keys(Object source);

	protected boolean containsKey(Object source, String key) {
		return getProperty(source, key) != null;
	}

	protected abstract Object getProperty(Object source, String key);

	private boolean canSetType(TypeDescriptor type) {
		if (type == null) {
			return true;
		}

		if (type.isPrimitive()) {
			return true;
		}

		if (type.isCollection() || type.isArray()) {
			return canSetType(type.getElementTypeDescriptor());
		} else if (type.isMap()) {
			return canSetType(type.getMapKeyTypeDescriptor()) && canSetType(type.getMapValueTypeDescriptor());
		}

		if (type.getType().isInterface() || Modifier.isAbstract(type.getType().getModifiers())) {
			return false;
		}
		return true;
	}

	public Structure<? extends Property> getStructure(Class<?> type, Field parentField) {
		Structure<? extends Property> structure = getMapper().getStructure(type).filter(FieldFeature.SUPPORT_SETTER)
				.filter((f) -> canSetType(TypeDescriptor.valueOf(f.getSetter().getGenericType())))
				.filter(getFieldAccept()).setParentField(parentField);
		if (isUseSuperClass()) {
			structure = structure.all();
		}
		return structure;
	}

	private void setValue(Field field, Object value, TypeDescriptor sourceType, Object target) {
		Object valueToUse = getConversionService().convert(value, sourceType.narrow(value),
				new TypeDescriptor(field.getSetter()));
		if (logger.isLoggable(loggerLevel)) {
			logger.log(loggerLevel, "Property {} on target {} set value {}", field.getSetter().getName(), target,
					valueToUse);
		}
		field.getSetter().set(target, valueToUse);
	}

	public void configurationProperties(Object source, Object target) {
		configurationProperties(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	private Collection<String> getUseSetterNames(AliasRegistry aliasRegistry, Class<?> entityClass, Field field) {
		List<String> useNames = new ArrayList<String>(8);
		Collection<String> names = field.getAliasNames();
		for (String name : names) {
			useNames.add(name);
			if (aliasRegistry != null) {
				for (String alias : aliasRegistry.getAliases(name)) {
					useNames.add(alias);
				}
			}
		}
		return useNames;
	}

	private String toUseName(String parentName, String name) {
		StringBuilder nameAppend = new StringBuilder(32);
		if (prefix != null) {
			nameAppend.append(prefix);
		}

		if (parentName != null) {
			nameAppend.append(parentName);
		}

		if (nameAppend.length() != 0) {
			nameAppend.append(connector);
		}

		nameAppend.append(name);
		return nameAppend.toString();
	}

	private void appendNames(Class<?> entityClass, @Nullable AliasRegistry aliasRegistry, List<String> names,
			String parentName, Property field) {
		Field parent = field.getParent();
		if (parent == null) {
			// 最顶层的字段
			Collection<String> aliasNames = field.getAliasNames();
			for (String name : aliasNames) {
				names.add(toUseName(parentName, name));
				if (aliasRegistry != null) {
					for (String alias : aliasRegistry.getAliases(name)) {
						names.add(toUseName(parentName, alias));
					}
				}
			}

			// 该字段的声明类
			for (String entityName : getMapper().getAliasNames(field.getSetter().getDeclaringClass())) {
				for (String alias : aliasNames) {
					names.add(toUseName(parentName, entityName + connector + alias));
					if (aliasRegistry != null) {
						for (String aliasName : aliasRegistry.getAliases(alias)) {
							names.add(toUseName(parentName, entityName + connector + aliasName));
						}
					}
				}
			}
		} else {
			for (String name : getUseSetterNames(aliasRegistry, entityClass, parent)) {
				appendNames(entityClass, aliasRegistry, names,
						parentName == null ? (name + connector) : (name + connector + parentName), field.getParent());
			}
		}
	}

	private Collection<String> getSetterNames(Class<?> entityClass, Property field) {
		List<String> names = new ArrayList<String>(8);
		appendNames(entityClass, getAliasRegistry(), names, null, field);
		return names;
	}

	public void configurationProperties(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) {
		if (source == null) {
			return;
		}

		String prefix = getPrefix();
		prefix = StringUtils.isEmpty(prefix) ? "" : (prefix + connector);
		Structure<? extends Property> targetFields = getStructure(targetType.getType(), parentField);
		if (isStrict()) {
			strictConfiguration(targetFields, source, sourceType, target);
		} else {
			noStrictConfiguration(source.getClass(), targetFields, source, sourceType, target);
		}
	}

	private void noStrictConfiguration(Class<?> clazz, Structure<? extends Property> structure, Object source,
			TypeDescriptor sourceType, Object target) {
		for (Property field : structure) {
			if (!field.isSupportSetter()) {
				continue;
			}

			Object value = null;
			if (field.isEntity()) {
				// 如果是一个实体
				Class<?> entityClass = field.getSetter().getType();
				value = getInstanceFactory().getInstance(entityClass);
				noStrictConfiguration(entityClass, getStructure(entityClass, field), source, sourceType, value);
			} else {
				value = getProperty(source, field);
			}
			if (value != null) {
				setValue(field, value, sourceType, target);
			}
		}
	}

	private void strictConfiguration(Structure<? extends Field> fields, Object source, TypeDescriptor sourceType,
			Object target) {
		Enumeration<String> keys = keys(source);
		while (keys.hasMoreElements()) {
			String originKey = keys.nextElement();
			String name = originKey;
			if (StringUtils.isNotEmpty(prefix) && name.startsWith(prefix)) {
				name = name.substring(prefix.length());
			}

			Field field = fields.getByName(name, null);
			if (field == null && aliasRegistry != null) {
				for (String alias : aliasRegistry.getAliases(name)) {
					field = fields.getByName(alias, null);
					if (field != null) {
						break;
					}
				}
			}

			if (field == null) {
				continue;
			}

			Object value = getProperty(source, originKey);
			if (value != null) {
				setValue(field, value, sourceType, target);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Object target = getInstanceFactory().getInstance(targetType.getType());
		configurationProperties(source, sourceType, target, targetType);
		return (R) target;
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !canDirectlyConvert(sourceType, targetType) && super.canConvert(sourceType, targetType)
				&& getInstanceFactory().isInstance(targetType.getType());
	}
}
