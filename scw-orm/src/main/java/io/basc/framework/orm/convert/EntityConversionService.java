package io.basc.framework.orm.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.OrmUtils;
import io.basc.framework.util.ConfigurableAccept;
import io.basc.framework.util.alias.AliasRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class EntityConversionService extends ConditionalConversionService implements ConversionServiceAware, FieldFactory {
	private static Logger logger = LoggerFactory.getLogger(EntityConversionService.class);
	private AliasRegistry aliasRegistry;
	private final ConfigurableAccept<Field> fieldAccept = new ConfigurableAccept<Field>();
	private String prefix;
	private String connector = ".";
	private boolean strict = false;
	private ConversionService conversionService;
	private NoArgsInstanceFactory instanceFactory;
	private Level loggerLevel = io.basc.framework.logger.Levels.DEBUG.getValue();
	private ObjectRelationalMapping objectRelationalMapping;
	private Field parentField;
	//是否先检查key存在
	private boolean checkKeyExists = false;
	//默认是所有字段
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

	public ObjectRelationalMapping getObjectRelationalMapping() {
		return objectRelationalMapping == null ? OrmUtils.getMapping() : objectRelationalMapping;
	}

	public void setObjectRelationalMapping(ObjectRelationalMapping objectRelationalMapping) {
		this.objectRelationalMapping = objectRelationalMapping;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory == null ? Sys.env : instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public final ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
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

				valueMap.put(StringUtils.isEmpty(prefix) ? key : key.substring(prefix.length() + connector.length()),
						value);
			}
		}
	}

	@Nullable
	private Object getProperty(Object source, Field field) {
		Collection<String> names = getSetterNames(field);
		if(logger.isTraceEnabled()) {
			logger.trace(field + " - " + names);
		}
		for (String name : names) {
			if (!checkKeyExists || containsKey(source, name)) {
				return getProperty(source, name);
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
	
	
	public Fields getFields(Class<?> type, Field parentField) {
		Fields fields = getObjectRelationalMapping().getFields(type, parentField)
				.accept(FieldFeature.SUPPORT_SETTER).accept(getFieldAccept());
		if(isUseSuperClass()) {
			fields = fields.all();
		}
		return fields;
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

	private Collection<String> getUseSetterNames(Field field) {
		List<String> useNames = new ArrayList<String>(8);
		Collection<String> names = getObjectRelationalMapping().getAliasNames(field.getSetter());
		for (String name : names) {
			useNames.add(name);
			for (String alias : getAliasRegistry().getAliases(name)) {
				useNames.add(alias);
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

	private void appendNames(@Nullable AliasRegistry aliasRegistry, List<String> names, String parentName,
			Field field) {
		Field parent = field.getParentField();
		if (parent == null) {
			//最顶层的字段
			Collection<String> aliasNames = getObjectRelationalMapping().getAliasNames(field.getSetter());
			for (String name : aliasNames) {
				names.add(toUseName(parentName, name));
				if (aliasRegistry != null) {
					for (String alias : aliasRegistry.getAliases(name)) {
						names.add(toUseName(parentName, alias));
					}
				}
			}
			
			//该字段的声明类
			Class<?> declaringClass = field.getSetter().getDeclaringClass();
			for(String entityName : getObjectRelationalMapping().getAliasNames(declaringClass)) {
				for(String alias : aliasNames) {
					names.add(toUseName(parentName, entityName + connector + alias));
					if (aliasRegistry != null) {
						for (String aliasName : aliasRegistry.getAliases(alias)) {
							names.add(toUseName(parentName, entityName + connector + aliasName));
						}
					}
				}
			}
		} else {
			for (String name : getUseSetterNames(parent)) {
				appendNames(aliasRegistry, names,
						parentName == null ? (name + connector) : (name + connector + parentName),
						field.getParentField());
			}
		}
	}

	private Collection<String> getSetterNames(Field field) {
		List<String> names = new ArrayList<String>(8);
		appendNames(getAliasRegistry(), names, null, field);
		return names;
	}

	public void configurationProperties(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) {
		if (source == null) {
			return;
		}

		String prefix = getPrefix();
		prefix = StringUtils.isEmpty(prefix) ? "" : (prefix + connector);
		Fields targetFields = getFields(targetType.getType(), parentField);
		if (isStrict()) {
			strictConfiguration(targetFields, source, sourceType, target);
		} else {
			noStrictConfiguration(targetFields, source, sourceType, target);
		}
	}

	private void noStrictConfiguration(Fields fields, Object source, TypeDescriptor sourceType, Object target) {
		for (Field field : fields) {
			Object value = null;
			if (getObjectRelationalMapping().isEntity(field.getSetter())) {
				// 如果是一个实体
				Class<?> entityClass = field.getSetter().getType();
				value = getInstanceFactory().getInstance(entityClass);
				noStrictConfiguration(getFields(entityClass, field), source, sourceType, value);
			} else {
				value = getProperty(source, field);
			}
			if (value != null) {
				setValue(field, value, sourceType, target);
			}
		}
	}

	private void strictConfiguration(Fields fields, Object source, TypeDescriptor sourceType, Object target) {
		Enumeration<String> keys = keys(source);
		while (keys.hasMoreElements()) {
			String originKey = keys.nextElement();
			String name = originKey;
			if (StringUtils.isNotEmpty(prefix) && name.startsWith(prefix)) {
				name = name.substring(prefix.length());
			}

			Field field = fields.find(name, null);
			if (field == null && aliasRegistry != null) {
				for (String alias : aliasRegistry.getAliases(name)) {
					field = fields.find(alias, null);
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

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Object target = getInstanceFactory().getInstance(targetType.getType());
		configurationProperties(source, sourceType, target, targetType);
		return target;
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !canDirectlyConvert(sourceType, targetType) && super.canConvert(sourceType, targetType)
				&& getInstanceFactory().isInstance(targetType.getType());
	}
}
