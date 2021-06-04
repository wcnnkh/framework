package scw.orm;

import java.util.Collection;

public interface EntityNameMapping {
	String getEntityName(Class<?> clazz);

	Collection<String> getSetterEntityNames(Class<?> entityClass);
}
