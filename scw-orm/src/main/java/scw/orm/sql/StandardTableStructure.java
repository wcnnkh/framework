package scw.orm.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import scw.mapper.Field;
import scw.orm.EntityStructure;
import scw.orm.Property;
import scw.orm.StandardEntityStructure;
import scw.util.Accept;

public class StandardTableStructure extends StandardEntityStructure<Column>
		implements TableStructure {
	private Map<String, List<Column>> indexGroup;

	public StandardTableStructure() {
	}

	public StandardTableStructure(
			EntityStructure<? extends Property> entityStructure) {
		setEntityClass(entityStructure.getEntityClass());
		setName(entityStructure.getName());
		setRows(entityStructure.stream()
				.map((property) -> new StandardColumn(property))
				.collect(Collectors.toList()));
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
		return wrapper(entityClass,
				(field) -> StandardEntityStructure.isEntity(field.getGetter())
						|| StandardEntityStructure.isEntity(field.getSetter()));
	}

	public static StandardTableStructure wrapper(Class<?> entityClass,
			Accept<Field> entityAccept) {
		return new StandardTableStructure(StandardEntityStructure.resolve(
				entityClass, entityAccept));
	}
}
