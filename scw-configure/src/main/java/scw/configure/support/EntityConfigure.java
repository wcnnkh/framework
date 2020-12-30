package scw.configure.support;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.configure.Configure;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mapper.EditableFieldFilters;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.Fields;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.alias.AliasRegistry;

public abstract class EntityConfigure extends ConditionalConfigure implements Configure{
	private static final String CONNECTOR = ".";
	private AliasRegistry aliasRegistry;
	private boolean ignoreStaticField = true;
	private final EditableFieldFilters fieldFilters = new EditableFieldFilters();
	private String prefix;
	private String connector;
	private final ConversionService conversionService;

	public EntityConfigure(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public AliasRegistry getAliasRegistry() {
		return aliasRegistry;
	}

	public void setAliasRegistry(AliasRegistry aliasRegistry) {
		this.aliasRegistry = aliasRegistry;
	}

	public boolean isIgnoreStaticField() {
		return ignoreStaticField;
	}

	public void setIgnoreStaticField(boolean ignoreStaticField) {
		this.ignoreStaticField = ignoreStaticField;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public EditableFieldFilters getFieldFilters() {
		return fieldFilters;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public String getConnector() {
		return StringUtils.isEmpty(connector) ? CONNECTOR : connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	private Map<String, Object> getMapByPrefix(Object source, String prefix,
			String connector) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<String> keys = keys(source);
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.equals(prefix) || map.containsKey(key)) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Object value = getProperty(source, key);
				if (value == null) {
					continue;
				}

				map.put(key.substring(prefix.length() + connector.length()),
						value);
			}
		}
		return map;
	}

	private Object getProperty(Object source, String name, String prefix, AliasRegistry aliasRegistry) {
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

	private Object getProperty(Object source, Field field, String prefix, AliasRegistry aliasRegistry) {
		String name = field.getSetter().getName();
		Object value = getProperty(source, name, prefix, aliasRegistry);
		if (value == null) {
			name = StringUtils.humpNamingReplacement(field.getSetter()
					.getName(), "-");
			value = getProperty(source, name, prefix, aliasRegistry);
		}
		return value;
	}

	private Map<String, Object> getMapProperty(Object source, Field field,
			String prefix, String connector) {
		Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
		valueMap.putAll(getMapByPrefix(source, prefix
				+ field.getSetter().getName(), connector));
		valueMap.putAll(getMapByPrefix(
				source,
				prefix
						+ StringUtils.humpNamingReplacement(field.getSetter()
								.getName(), "-"), connector));
		return valueMap;
	}
	
	protected abstract Enumeration<String> keys(Object source);

	protected abstract Object getProperty(Object source, String key);
	
	public Fields getFields(Class<?> type){
		Fields fields;
		if (isIgnoreStaticField()) {
			fields = MapperUtils.getMapper().getFields(type,
					FilterFeature.EXISTING_SETTER_FIELD.getFilter(),
					FilterFeature.IGNORE_STATIC.getFilter(),
					(FieldFilter) getFieldFilters());
		} else {
			fields = MapperUtils.getMapper().getFields(type,
					FilterFeature.EXISTING_SETTER_FIELD.getFilter(),
					(FieldFilter) getFieldFilters());
		}
		return fields;
	}
	
	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		if(source == null){
			return ;
		}
		
		String connector = getConnector();
		String prefix = getPrefix();
		prefix = StringUtils.isEmpty(prefix) ? "" : (prefix + connector);
		for (Field field : getFields(targetType.getType())) {
			Object value = getProperty(source, field, prefix, getAliasRegistry());
			if (value == null) {
				if (field.getSetter().getType() == Map.class) {
					Map<String, Object> valueMap = getMapProperty(source,
							field, prefix, connector);
					if (!CollectionUtils.isEmpty(valueMap)) {
						value = valueMap;
					}
				}
			}
			
			if(value != null){
				value = conversionService.convert(value, sourceType.narrow(value),
						new TypeDescriptor(field.getSetter()));
				field.getSetter().set(target, value);
			}
		}
	}
}
