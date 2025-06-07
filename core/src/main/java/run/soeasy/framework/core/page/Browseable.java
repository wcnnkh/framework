package run.soeasy.framework.core.page;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;

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

	default Browseable<K, T> filter(@NonNull Predicate<? super T> predicate) {
		return convert((elements) -> elements.filter(predicate));
	}

	default <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Codec.identity(), elementMapper);
	}

	default <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec,
			@NonNull Function<? super T, ? extends TT> elementMapper) {
		return convert(cursorIdCodec, (elements) -> elements.map(elementMapper));
	}

	default <TT> Browseable<K, TT> flatMap(@NonNull Function<? super T, ? extends Elements<TT>> mapper) {
		return convert((elements) -> elements.flatMap(mapper));
	}

	default <TT> Browseable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Codec.identity(), elementsConverter);
	}

	default <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleBrowseable<>(this, cursorIdCodec, elementsConverter);
	}
}