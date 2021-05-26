package scw.orm.convert;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.instance.NoArgsInstanceFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.orm.annotation.Entity;
import scw.util.ConfigurableAccept;
import scw.util.alias.AliasRegistry;

public abstract class EntityConversionService extends ConditionalConversionService implements ConversionServiceAware {
	private static Logger logger = LoggerFactory.getLogger(EntityConversionService.class);
	private AliasRegistry aliasRegistry;
	private boolean ignoreStaticField = true;
	private final ConfigurableAccept<Field> fieldAccept = new ConfigurableAccept<Field>();
	private String prefix;
	private String connector = ".";
	private boolean strict = false;
	private ConversionService conversionService;
	private NoArgsInstanceFactory instanceFactory;
	private Level loggerLevel = scw.logger.Levels.DEBUG.getValue();
	private boolean useSuperClass = true;// 默认也使用父类
	private boolean ignoreFinalField = false;// 是否忽略常量字段

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public void setInstanceFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isIgnoreFinalField() {
		return ignoreFinalField;
	}

	public final ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setIgnoreFinalField(boolean ignoreFinalField) {
		this.ignoreFinalField = ignoreFinalField;
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

	public boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public void setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
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

	private Map<String, Object> getMapByPrefix(Object source, String prefix, String connector) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<String> keys = keys(source);
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || map.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Object value = getProperty(source, key);
				if (value == null) {
					continue;
				}

				map.put(StringUtils.isEmpty(prefix) ? key : key.substring(prefix.length() + connector.length()), value);
			}
		}
		return map;
	}

	private Object getProperty(Object source, String name, String prefix) {
		Object value = getProperty(source, prefix + name);
		if (value == null && aliasRegistry != null) {
			String[] names = aliasRegistry.getAliases(name);
			if (!ArrayUtils.isEmpty(names)) {
				for (String n : names) {
					value = getProperty(source, prefix + n);
					if (value != null) {
						break;
					}
				}
			}
		}
		return value;
	}

	private Object getProperty(Object source, Field field, String prefix) {
		String name = field.getSetter().getName();
		Object value = getProperty(source, name, prefix);
		if (value == null) {
			name = StringUtils.humpNamingReplacement(field.getSetter().getName(), "-");
			value = getProperty(source, name, prefix);
		}
		return value;
	}

	private Map<String, Object> getMapProperty(Object source, Field field, String prefix, String connector) {
		Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
		valueMap.putAll(getMapByPrefix(source, prefix + field.getSetter().getName(), connector));
		valueMap.putAll(getMapByPrefix(source,
				prefix + StringUtils.humpNamingReplacement(field.getSetter().getName(), "-"), connector));
		return valueMap;
	}

	/**
	 * 将source转化为map并插入到targetMap
	 * 
	 * @param source
	 * @param targetMap
	 * @param keyType
	 * @param valueType
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void putAll(Object source, Map targetMap, TypeDescriptor keyType, TypeDescriptor valueType) {
		Map<String, Object> sourceMap = getMapByPrefix(source, prefix, connector);
		for (Entry<String, Object> entry : sourceMap.entrySet()) {
			Object key = getConversionService().convert(entry.getKey(), TypeDescriptor.valueOf(String.class), keyType);
			Object value = getConversionService().convert(entry.getValue(), TypeDescriptor.forObject(entry.getValue()),
					valueType);
			targetMap.put(key, value);
		}
	}

	protected abstract Enumeration<String> keys(Object source);

	protected abstract Object getProperty(Object source, String key);

	protected Fields getFields(Class<?> type) {
		Fields fields = MapperUtils.getMapper().getFields(type, isUseSuperClass())
				.accept(FieldFeature.EXISTING_SETTER_FIELD);
		if (isIgnoreStaticField()) {
			fields = fields.accept(FieldFeature.IGNORE_STATIC);
		}

		if (isIgnoreFinalField()) {
			fields = fields.accept(FieldFeature.IGNORE_SETTER_FINAL);
		}
		return fields.accept(fieldAccept);
	}

	private void setValue(Field field, Object value, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) {
		Object valueToUse = getConversionService().convert(value, sourceType.narrow(value),
				new TypeDescriptor(field.getSetter()));
		if (logger.isLoggable(loggerLevel)) {
			logger.log(loggerLevel, "Property {} on target {} set value {}", field.getSetter().getName(),
					targetType.getType(), valueToUse);
		}
		field.getSetter().set(target, valueToUse);
	}

	public void configurationProperties(Object source, Object target) {
		configurationProperties(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	public void configurationProperties(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) {
		if (source == null) {
			return;
		}

		String connector = getConnector();
		String prefix = getPrefix();
		prefix = StringUtils.isEmpty(prefix) ? "" : (prefix + connector);
		Fields fields = getFields(targetType.getType());
		if (isStrict()) {
			strictConfiguration(fields, source, sourceType, target, targetType, prefix);
		} else {
			noStrictConfiguration(fields, source, sourceType, target, targetType, prefix, connector);
		}
	}

	private void noStrictConfiguration(Fields fields, Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, String prefix, String connector) {
		for (Field field : fields) {
			Object value = null;
			if (field.isAnnotationPresent(Entity.class)
					|| field.getSetter().getType().isAnnotationPresent(Entity.class)) {
				// 如果是一个实体
				String entityPrefix = prefix + connector + field.getSetter().getName();
				Class<?> entityClass = field.getSetter().getType();
				value = getInstanceFactory().getInstance(entityClass);
				noStrictConfiguration(getFields(entityClass), source, sourceType, value,
						new TypeDescriptor(field.getSetter()), entityPrefix, connector);
			} else {
				value = getProperty(source, field, prefix);
				if (value == null) {
					if (Map.class.isAssignableFrom(field.getSetter().getType())) {
						Map<String, Object> valueMap = getMapProperty(source, field, prefix, connector);
						if (!CollectionUtils.isEmpty(valueMap)) {
							value = valueMap;
						}
					}
				}
			}
			if (value != null) {
				setValue(field, value, sourceType, target, targetType);
			}
		}
	}

	protected void strictConfiguration(Fields fields, Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, String prefix) {
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
				setValue(field, value, sourceType, target, targetType);
			}
		}
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Object target = newInstance(targetType);
		configurationProperties(source, sourceType, target, targetType);
		return target;
	}

	protected boolean isInstance(TypeDescriptor targetType) {
		if (instanceFactory == null) {
			return ReflectionUtils.isInstance(targetType.getType());
		}
		return instanceFactory.isInstance(targetType.getType());
	}

	protected Object newInstance(TypeDescriptor targetType) {
		if (instanceFactory == null) {
			return ReflectionUtils.newInstance(targetType.getType());
		}
		return instanceFactory.getInstance(targetType.getType());
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !canDirectlyConvert(sourceType, targetType) && super.canConvert(sourceType, targetType)
				&& isInstance(targetType);
	}
}
