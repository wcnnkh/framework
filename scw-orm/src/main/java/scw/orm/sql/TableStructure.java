package scw.orm.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scw.mapper.Field;

public interface TableStructure extends Iterable<Column> {
	Class<?> getEntityClass();
	
	@Override
	default Iterator<Column> iterator() {
		return stream().iterator();
	}

	default Stream<Column> stream() {
		return getColumns().stream();
	}

	String getName();

	List<Column> getColumns();
	
	default Column getColumn(Field field) {
		return stream().filter((column) -> field.equals(column.getField())).findFirst().orElse(null);
	}

	Map<String, List<Column>> getIndexGroup();

	default List<Column> getPrimaryKeys() {
		return stream().filter((column) -> column.isPrimaryKey()).collect(Collectors.toList());
	}

	default List<Column> getNotPrimaryKeys() {
		return stream().filter((column) -> !column.isPrimaryKey()).collect(Collectors.toList());
	}

	default TableStructure rename(String name) {
		return new TableStructureWrapper(this) {
			@Override
			public String getName() {
				return name;
			}
		};
	}
}
