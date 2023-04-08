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
	 * 数量限制
	 * 
	 * @return
	 */
	long getLimit();

	default Page<K, T> shared() {
		return new SharedPage<>(this);
	}

	@Override
	default <TT> Page<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return map(Function.identity(), valueMapper);
	}

	@Override
	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return new ConvertiblePage<>(this, keyMapper, valueMapper);
	}
}
