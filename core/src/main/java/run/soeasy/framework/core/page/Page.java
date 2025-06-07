package run.soeasy.framework.core.page;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;

public interface Page<K, T> extends Cursor<K, T> {

	long getTotal();

	long getPageSize();

	default Page<K, T> shared() {
		return new SharedPage<>(this);
	}

	@Override
	default Page<K, T> filter(@NonNull Predicate<? super T> predicate) {
		return convert((e) -> e.filter(predicate));
	}

	default <TT> Page<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Function.identity(), elementMapper);
	}

	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> cursorIdMapper,
			@NonNull Function<? super T, ? extends TT> elementMapper) {
		return convert(cursorIdMapper, (elements) -> elements.map(elementMapper));
	}

	default <TT> Page<K, TT> flatMap(@NonNull Function<? super T, ? extends Elements<TT>> mapper) {
		return convert((elements) -> elements.flatMap(mapper));
	}

	default <TT> Page<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Function.identity(), elementsConverter);
	}

	default <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertiblePage<>(this, cursorIdConverter, elementsConverter);
	}
}
