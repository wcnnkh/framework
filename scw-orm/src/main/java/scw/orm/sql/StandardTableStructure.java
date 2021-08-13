package scw.orm.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.MapperUtils;
import scw.orm.OrmUtils;
import scw.util.Accept;
import scw.value.Value;

public class StandardTableStructure implements TableStructure {
	private Class<?> entityClass;
	private String name;
	private List<Column> columns;
	private Map<String, List<Column>> indexGroup;

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

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Map<String, List<Column>> getIndexGroup() {
		if (indexGroup == null) {
			return Collections.emptyMap();
		}
		return indexGroup;
	}

	public void setIndexGroup(Map<String, List<Column>> indexGroup) {
		this.indexGroup = indexGroup;
	}

	public static StandardTableStructure wrapper(Class<?> entityClass) {
		return wrapper(entityClass, (field) -> isEntity(field.getGetter()));
	}

	public static boolean isEntity(FieldDescriptor fieldDescriptor) {
		Class<?> type = fieldDescriptor.getType();
		return !(Value.isBaseType(type) || type.isArray()
				|| Collection.class.isAssignableFrom(type) || Map.class
					.isAssignableFrom(type))
				|| OrmUtils.getMapping().isEntity(fieldDescriptor);
	}

	public static StandardTableStructure wrapper(Class<?> entityClass,
			Accept<Field> entityAccept) {
		StandardTableStructure standardTableStructure = new StandardTableStructure();
		standardTableStructure.setName(OrmUtils.getMapping().getName(
				entityClass));
		standardTableStructure.setEntityClass(entityClass);
		standardTableStructure.setColumns(StandardColumn.wrapper(OrmUtils
				.getMapping().getFields(entityClass), entityAccept));
		return standardTableStructure;
	}
	
	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
