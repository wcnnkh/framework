package run.soeasy.framework.core.page;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;

public interface Cursor<K, T> {

	K getCursorId();

	K getNextCursorId();

	Elements<T> getElements();

	default boolean hasNext() {
		return getNextCursorId() != null;
	}

	default Cursor<K, T> shared() {
		return new SharedCursor<>(this);
	}

	default Cursor<K, T> filter(@NonNull Predicate<? super T> predicate) {
		return convert((elements) -> elements.filter(predicate));
	}

	default <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Function.identity(), elementMapper);
	}

	default <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> cursorIdMapper,
			@NonNull Function<? super T, ? extends TT> elementMapper) {
		return convert(cursorIdMapper, (elements) -> elements.map(elementMapper));
	}

	default <TT> Cursor<K, TT> flatMap(@NonNull Function<? super T, ? extends Elements<TT>> mapper) {
		return convert((elements) -> elements.flatMap(mapper));
	}

	default <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Function.identity(), elementsConverter);
	}

	default <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleCursor<>(this, cursorIdConverter, elementsConverter);
	}
}