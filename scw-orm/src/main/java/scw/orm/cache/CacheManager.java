package scw.orm.cache;

import scw.orm.EntityOperations;

public interface CacheManager extends EntityOperations {
	/**
	 * 是否继续查找
	 * 
	 * @param entityClass
	 * @param ids
	 * @return
	 */
	boolean isKeepLooking(Class<?> entityClass, Object... ids);
}
