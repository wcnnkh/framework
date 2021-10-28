package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.mapper.Field;

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
}
