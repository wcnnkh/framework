package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public interface Page<K, T> extends Cursor<K, T> {
	/**
	 * 总数
	 * 
	 * @return
	 */
	long getTotal();

	/**
	 * 每页大小
	 * 
	 * @return
	 */
	long getPageSize();

	default Page<K, T> shared() {
		return new SharedPage<>(this);
	}

	@Override
	default Page<K, T> filter(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((e) -> e.filter(predicate));
	}

	/**
	 * 默认调用{@link #map(Function, Function)}
	 * 
	 * @param <TT>
	 * @param elementMapper
	 * @return
	 */
	default <TT> Page<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Function.identity(), elementMapper);
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TK>
	 * @param <TT>
	 * @param cursorIdMapper
	 * @param elementMapper
	 * @return
	 */
	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> cursorIdMapper,
			Function<? super T, ? extends TT> elementMapper) {
		Assert.requiredArgument(elementMapper != null, "elementMapper");
		return convert(cursorIdMapper, (elements) -> elements.map(elementMapper));
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param <TT>
	 * @param mapper
	 * @return
	 */
	default <TT> Page<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.flatMap(mapper));
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TT>
	 * @param elementsConverter
	 * @return
	 */
	default <TT> Page<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Function.identity(), elementsConverter);
	}

	default <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertiblePage<>(this, cursorIdConverter, elementsConverter);
	}
}
