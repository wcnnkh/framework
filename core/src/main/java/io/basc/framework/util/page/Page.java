package io.basc.framework.util.page;

import java.util.function.Function;

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

	@Override
	default <TT> Page<K, TT> map(Function<? super T, ? extends TT> map) {
		return map(Function.identity(), map);
	}

	@Override
	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMap, Function<? super T, ? extends TT> valueMap) {
		return new MapPage<>(this, keyMap, valueMap);
	}
}
