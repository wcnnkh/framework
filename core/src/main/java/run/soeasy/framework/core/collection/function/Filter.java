package run.soeasy.framework.core.collection.function;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;

/**
 * 过滤器
 * 
 * @author soeasy.run
 *
 * @param <T>
 */
@FunctionalInterface
public interface Filter<T> extends Function<Elements<T>, Elements<T>> {
	@RequiredArgsConstructor
	public static class PredicateFilter<T> implements Filter<T> {
		private static final Filter<?> IGNORE_NULL = new PredicateFilter<>((e) -> e != null);

		@NonNull
		private final Predicate<? super T> predicate;

		@Override
		public Elements<T> apply(@NonNull Elements<T> elements) {
			return elements.filter(predicate);
		}
	}

	public static <T> Filter<T> forPredicate(@NonNull Predicate<? super T> predicate) {
		return new PredicateFilter<>(predicate);
	}

	static <T> Filter<T> identity() {
		return t -> t;
	}

	@SuppressWarnings("unchecked")
	public static <T> Filter<T> ignoreNull() {
		return (Filter<T>) PredicateFilter.IGNORE_NULL;
	}

	@Override
	Elements<T> apply(Elements<T> elements);
}
