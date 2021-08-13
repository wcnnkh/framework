package scw.orm.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import scw.mapper.Field;
import scw.orm.OrmUtils;
import scw.util.Accept;

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

	public static StandardTableStructure wrapper(Class<?> entityClass, Accept<Field> entityAccept) {
		StandardTableStructure standardTableStructure = new StandardTableStructure();
		List<Column> columns = new ArrayList<>();
		standardTableStructure.setName(OrmUtils.getMapping().getName(entityClass));
		standardTableStructure.setEntityClass(entityClass);
		columns.addAll(StandardColumn.wrapper(OrmUtils.getMapping().getFields(entityClass), entityAccept));
		return standardTableStructure;
	}
}
