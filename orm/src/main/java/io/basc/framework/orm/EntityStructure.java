package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * 实体结构
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface EntityStructure<T extends Property> extends
		EntityDescriptor<T>, Iterable<T> {
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
		return stream().filter((column) -> field.equals(column.getField()))
				.findFirst().orElse(null);
	}

	default Stream<T> byName(String name) {
		return stream()
				.map((e) -> {
					if (StringUtils.equals(e.getName(), name)) {
						return new Pair<>(e, 1);
					} else if (e.getAliasNames() != null
							|| e.getAliasNames().contains(name)) {
						return new Pair<>(e, 2);
					} else if (e.getField() != null
							&& e.getField().isSupportGetter()
							&& e.getField().getGetter().getName().equals(name)) {
						return new Pair<>(e, 3);
					} else if (e.getField() != null
							&& e.getField().isSupportSetter()
							&& e.getField().getSetter().getName().equals(name)) {
						return new Pair<>(e, 4);
					}
					return null;
				}).filter((e) -> e != null)
				.sorted(Comparator.comparingInt(Pair::getValue))
				.map((e) -> e.getKey());
	}

	default T getByName(String name) throws NoSuchElementException {
		return byName(name).findFirst().get();
	}

	default Field getFieldByName(String name) throws NoSuchElementException {
		return byName(name).filter((e) -> e.getField() != null).findFirst()
				.get().getField();
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
