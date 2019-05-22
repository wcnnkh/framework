package scw.db.cache;

public interface LazyCacheConfig {
	/**
	 * 过期时间
	 * @return
	 */
	int getExp();

	/**
	 * 是否缓存key以防止缓存雪崩
	 * @return
	 */
	boolean isKeys();

	/**
	 * 是否禁用缓存
	 * @return
	 */
	boolean isDisable();
}
