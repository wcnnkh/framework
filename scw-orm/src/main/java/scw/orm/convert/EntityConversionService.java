package scw.orm.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.instance.NoArgsInstanceFactory;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.orm.ObjectRelationalMapping;
import scw.orm.OrmUtils;
import scw.util.ConfigurableAccept;
import scw.util.alias.AliasRegistry;

public abstract class EntityConversionService extends ConditionalConversionService implements ConversionServiceAware {
	private static Logger logger = LoggerFactory.getLogger(EntityConversionService.class);
	private AliasRegistry aliasRegistry;
	private final ConfigurableAccept<Field> fieldAccept = new ConfigurableAccept<Field>();
	private String prefix;
	private String connector = ".";
	private boolean strict = false;
	private ConversionService conversionService;
	private NoArgsInstanceFactory instanceFactory;
	private Level loggerLevel = scw.logger.Levels.DEBUG.getValue();
	private ObjectRelationalMapping objectRelationalMapping;
	private boolean useSuperClass = true;// 默认也使用父类
	private Field parentField;

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

	public boolean isUseSuperClass() {
		return useSuperClass;
	}

	public void setUseSuperClass(boolean useSuperClass) {
		this.useSuperClass = useSuperClass;
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
		for (String name : names) {
			if (containsKey(source, name)) {
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

	protected Fields getFields(Class<?> type, Field parentField) {
		return getObjectRelationalMapping().getFields(type, isUseSuperClass(), parentField).accept(getFieldAccept());
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
		Collection<String> names = getObjectRelationalMapping().getSetterNames(field);
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

	private void appendNames(List<String> names, String parentName, Field field) {
		Field parent = field.getParentField();
		if (parent == null) {
			for (String name : getObjectRelationalMapping().getSetterNames(field)) {
				names.add(toUseName(parentName, name));
				for (String alias : getAliasRegistry().getAliases(name)) {
					names.add(toUseName(parentName, alias));
				}
			}

			for (String name : getObjectRelationalMapping().getSetterEntityNames(field.getSetter().getType())) {
				names.add(toUseName(parentName, name));
				for (String alias : getAliasRegistry().getAliases(name)) {
					names.add(toUseName(parentName, alias));
				}
			}
		} else {
			for (String name : getUseSetterNames(parent)) {
				appendNames(names, parentName == null ? (name + connector) : (name + connector + parentName),
						field.getParentField());
			}
		}
	}

	private Collection<String> getSetterNames(Field field) {
		List<String> names = new ArrayList<String>(8);
		appendNames(names, null, field);
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
			if (getObjectRelationalMapping().isEntity(field)) {
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
