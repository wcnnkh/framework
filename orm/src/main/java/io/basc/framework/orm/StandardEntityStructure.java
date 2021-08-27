package io.basc.framework.orm;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.Accept;
import io.basc.framework.value.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StandardEntityStructure<T extends Property> implements EntityStructure<T> {
	private Class<?> entityClass;
	private String name;
	private List<T> rows;
	
	public StandardEntityStructure(){
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getRows() {
		if (rows == null) {
			return Collections.emptyList();
		}
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
	
	public static boolean isEntity(FieldDescriptor fieldDescriptor){
		Class<?> type = fieldDescriptor.getType();
		return !(Value.isBaseType(type) || type.isArray()
				|| Collection.class.isAssignableFrom(type) || Map.class
					.isAssignableFrom(type));
	}
	
	public static EntityStructure<Property> resolve(Class<?> entityClass){
		return resolve(entityClass, (field) -> isEntity(field.getGetter()) || isEntity(field.getSetter()));
	}
	
	public static EntityStructure<Property> resolve(Class<?> entityClass, Accept<Field> entityAccept){
		StandardEntityStructure<Property> standardEntityStructure = new StandardEntityStructure<Property>();
		standardEntityStructure.setRows(StandardProperty.resolve(MapperUtils.getFields(entityClass), entityAccept));
		standardEntityStructure.setEntityClass(entityClass);
		standardEntityStructure.setName(StringUtils.toLowerCase(entityClass.getSimpleName(), 0, 1));
		return standardEntityStructure;
	}
}
