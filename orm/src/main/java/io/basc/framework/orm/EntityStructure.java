package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * 实体结构
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface EntityStructure<T extends Property> extends EntityDescriptor<T>, Iterable<T> {
	Class<?> getEntityClass();

	Collection<String> getAliasNames();

	default EntityStructure<T> rename(String name) {
		return new EntityStructureWrapper<EntityStructure<T>, T>(this) {

			@Override
			public String getName() {
				return name;
			}
		};
	}

	default T find(Field field) {
		return stream().filter((column) -> field.equals(column.getField())).findFirst().orElse(null);
	}

	default T getByFieldName(String name) throws NoSuchElementException {
		return stream()
				.filter((e) -> e.getField() != null
						&& ((e.getField().isSupportGetter() && e.getField().getGetter().getName().equals(name))
								|| (e.getField().isSupportSetter() && e.getField().getSetter().getName().equals(name))))
				.findFirst().get();
	}

	/**
	 * 获取所有的列(排除实体字段)
	 * 
	 * @return
	 */
	default Stream<T> columns() {
		return stream().filter((c) -> !c.isEntity());
	}
}
