package shuchaowen.core.db.storage.cache;

public enum CacheType {
	no,
	/**
	 * 延迟加载到缓存
	 */
	lazy,
	/**
	 * 延迟加载到缓存，但先加载所有的主键再缓存
	 */
	keys,
	/**
	 * 保存所有数据到缓存
	 */
	full
}
