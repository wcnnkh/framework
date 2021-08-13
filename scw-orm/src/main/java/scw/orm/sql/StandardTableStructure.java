package scw.orm.sql;

import java.util.List;
import java.util.Map;

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
		return indexGroup;
	}

	public void setIndexGroup(Map<String, List<Column>> indexGroup) {
		this.indexGroup = indexGroup;
	}
}
