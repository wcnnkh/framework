package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

public interface MaxValueFactory {
	<T> T getMaxValue(Class<? extends T> type, Class<?> entityClass, Field field);
}
