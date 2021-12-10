package io.basc.framework.util.page;

public interface Page<K, T> extends Pageable<K, T> {
	/**
	 * 总数
	 * 
	 * @return
	 */
	long getTotal();

	/**
	 * 分页数量
	 * 
	 * @return
	 */
	long getCount();

	default Page<K, T> shared() {
		return new SharedPage<>(this);
	}
}
