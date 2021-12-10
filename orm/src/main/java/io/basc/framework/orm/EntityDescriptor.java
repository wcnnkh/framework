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

	/**
	 * 获取所有的列, 属性不一定是列
	 * 
	 * @see #stream()
	 * @return
	 */
	default Stream<T> columns() {
		return stream();
	}

	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	/**
	 * 获取主键列
	 * 
	 * @see #columns()
	 * @return
	 */
	default List<T> getPrimaryKeys() {
		return columns().filter((column) -> column.isPrimaryKey()).collect(Collectors.toList());
	}

	/**
	 * 获取非主键列
	 * 
	 * @see #columns()
	 * @return
	 */
	default List<T> getNotPrimaryKeys() {
		return columns().filter((column) -> !column.isPrimaryKey()).collect(Collectors.toList());
	}
}
