package io.basc.framework.orm.cache;

import io.basc.framework.orm.EntityOperations;

public interface CacheManager extends EntityOperations {
	boolean isKeepLooking(Class<?> entityClass, Object... ids);
}
