package scw.orm;

import scw.mapper.Field;

public interface MaxValueFactory {
	<T> T getMaxValue(Class<? extends T> type, Class<?> entityClass, Field field);
}
