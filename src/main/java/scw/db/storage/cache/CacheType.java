package scw.db.storage.cache;

public enum CacheType {
	/**
	 * 不使用缓存
	 */
	no,
	/**
	 * 缓存的默认实现
	 * 延迟加载到缓存
	 */
	lazy,
	/**
	 * 相对于full可以节约内存
	 * 延迟加载到缓存，但先加载所有的主键再缓存
	 */
	keys,
	/**
	 * 保存所有数据到缓存
	 */
	full
}
