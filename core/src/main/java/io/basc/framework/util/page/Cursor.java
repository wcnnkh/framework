package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public interface Cursor<K, T> {
	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	@Nullable
	K getCursorId();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();

	Elements<T> getElements();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	default boolean hasNext() {
		return getNextCursorId() != null;
	}

	default Cursor<K, T> shared() {
		return new SharedCursor<>(this);
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Cursor<K, T> filter(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.filter(predicate));
	}

	/**
	 * 默认调用{@link #map(Function, Function)}
	 * 
	 * @param <TT>
	 * @param elementMapper
	 * @return
	 */
	default <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
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
	default <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> cursorIdMapper,
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
	default <TT> Cursor<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
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
	default <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Function.identity(), elementsConverter);
	}

	default <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleCursor<>(this, cursorIdConverter, elementsConverter);
	}
}