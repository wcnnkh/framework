package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public interface Pages<K, T> extends Page<K, T>, Pageables<K, T> {

	@Override
	default Pages<K, T> next() {
		return jumpTo(getNextCursorId());
	}

	default Pages<K, T> jumpTo(K cursorId) {
		return jumpTo(cursorId, getPageSize());
	}

	/**
	 * 获取所有页
	 */
	default Elements<? extends Page<K, T>> pages() {
		return Elements.of(() -> new PageablesIterator<>(this, (e) -> e.next()));
	}

	Pages<K, T> jumpTo(K cursorId, long count);

	@Override
	default Page<K, T> all() {
		return new AllPage<>(this);
	}

	/**
	 * 这是极端情况下的处理，不推荐使用(性能低下)
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	default Paginations<T> toPaginations(long start, long limit) {
		Paginations<T> paginations = new Paginations<>(all().getElements());
		paginations.setTotal(paginations.getTotal());
		paginations.setCursorId(start);
		paginations.setLimit(limit);
		return paginations;
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Pages<K, T> filter(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.filter(predicate));
	}

	/**
	 * 默认调用{@link #map(Codec, Function)}
	 * 
	 * @param <TT>
	 * @param elementMapper
	 * @return
	 */
	default <TT> Pages<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Codec.identity(), elementMapper);
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TK>
	 * @param <TT>
	 * @param cursorIdCodec
	 * @param elementMapper
	 * @return
	 */
	default <TK, TT> Pages<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		Assert.requiredArgument(elementMapper != null, "elementMapper");
		return convert(cursorIdCodec, (elements) -> elements.map(elementMapper));
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param <TT>
	 * @param mapper
	 * @return
	 */
	default <TT> Pages<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.flatMap(mapper));
	}

	/**
	 * 默认调用{@link #convert(Codec, Function)}
	 * 
	 * @param <TT>
	 * @param elementsConverter
	 * @return
	 */
	default <TT> Pages<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Codec.identity(), elementsConverter);
	}

	default <TK, TT> Pages<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertiblePages<>(this, cursorIdCodec, elementsConverter);
	}
}
