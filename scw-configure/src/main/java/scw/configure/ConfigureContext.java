package scw.configure;

import scw.mapper.EditableFieldFilters;
import scw.mapper.FieldFilter;
import scw.mapper.Fields;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.alias.AliasRegistry;
import scw.util.attribute.DefaultAttributes;

public class ConfigureContext extends DefaultAttributes<Object, Object> {
	private AliasRegistry aliasRegistry;
	private final EditableFieldFilters filters = new EditableFieldFilters();
	// 默认忽略静态字段
	private boolean ignoreStaticField = true;
	private String prefix;

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

	public EditableFieldFilters getFilters() {
		return filters;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public Fields getFields(Class<?> type){
		Fields fields;
		if (isIgnoreStaticField()) {
			fields = MapperUtils.getMapper().getFields(type,
					FilterFeature.EXISTING_SETTER_FIELD.getFilter(),
					FilterFeature.IGNORE_STATIC.getFilter(),
					(FieldFilter) getFilters());
		} else {
			fields = MapperUtils.getMapper().getFields(type,
					FilterFeature.EXISTING_SETTER_FIELD.getFilter(),
					(FieldFilter) getFilters());
		}
		return fields;
	}
}
