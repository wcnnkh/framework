package io.basc.framework.orm;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface EntityDescriptor<T extends PropertyMetadata> extends EntityMetadata, Iterable<T> {
	List<T> getProperties();

	default Stream<T> stream() {
		return getProperties().stream();
	}

	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	default List<T> getPrimaryKeys() {
		return stream().filter((column) -> column.isPrimaryKey()).collect(Collectors.toList());
	}

	default List<T> getNotPrimaryKeys() {
		return stream().filter((column) -> !column.isPrimaryKey()).collect(Collectors.toList());
	}
}
