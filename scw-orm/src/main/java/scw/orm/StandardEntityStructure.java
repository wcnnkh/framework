package scw.orm;

import java.util.Collections;
import java.util.List;

import scw.mapper.MapperUtils;

public class StandardEntityStructure<T extends Property> implements EntityStructure<T> {
	private Class<?> entityClass;
	private String name;
	private List<T> rows;

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
}
