package scw.db.cache;

public enum CacheType {
	/**
	 * 懒加载缓存，支持事务
	 */
	lazy, 
	/**
	 * 在懒加载的基础上缓存所有的key， 不支持事务
	 */
	keys, 
	/**
	 * 缓存所有数据，不支持事务
	 */
	full;
}
