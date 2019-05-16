package scw.db.cache;

public enum CacheType {
	/**
	 * 懒加载缓存
	 */
	lazy, 
	/**
	 * 在懒加载的基础上缓存所有的key
	 */
	keys, 
	/**
	 * 缓存所有数据 不支持事务
	 */
	full;
}
