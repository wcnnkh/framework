package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实体结构
 * @author shuchaowen
 *
 * @param <T>
 */
public interface EntityStructure<T extends Property> extends Iterable<T> {
	Class<?> getEntityClass();

	String getName();

	List<T> getRows();

	default EntityStructure<T> rename(String name) {
		return new EntityStructureWrapper<EntityStructure<T>, T>(this) {

			@Override
			public String getName() {
				return name;
			}
		};
	}

	default T find(Field field) {
		return stream().filter((column) -> field.equals(column.getField()))
				.findFirst().orElse(null);
	}

	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	default Stream<T> stream() {
		return getRows().stream();
	}

	default List<T> getPrimaryKeys() {
		return stream().filter((column) -> column.isPrimaryKey()).collect(
				Collectors.toList());
	}

	default List<T> getNotPrimaryKeys() {
		return stream().filter((column) -> !column.isPrimaryKey()).collect(
				Collectors.toList());
	}
}
