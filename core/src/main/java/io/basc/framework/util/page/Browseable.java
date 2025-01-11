package io.basc.framework.util.page;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.collections.Elements;

public interface Browseable<K, T> extends Cursor<K, T> {
	Browseable<K, T> jumpTo(K cursorId);

	default Browseable<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return jumpTo(getNextCursorId());
	}

	default Elements<? extends Cursor<K, T>> pages() {
		return Elements.of(() -> new BrowseableIterator<>(this, (e) -> e.next()));
	}

	default Cursor<K, T> all() {
		return new AllCursor<>(this);
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Browseable<K, T> filter(Predicate<? super T> predicate) {
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
	default <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
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
	default <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec,
			Function<? super T, ? extends TT> elementMapper) {
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
	default <TT> Browseable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
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
	default <TT> Browseable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Codec.identity(), elementsConverter);
	}

	default <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleBrowseable<>(this, cursorIdCodec, elementsConverter);
	}
}