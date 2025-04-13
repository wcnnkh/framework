package run.soeasy.framework.core.function;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;

/**
 * 多对多
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
@FunctionalInterface
public interface Filter<E> extends UnaryOperator<Elements<E>> {
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

	public static <T> Filter<T> forSelector(@NonNull Selector<T> selector) {
		return (elements) -> {
			T target = selector.apply(elements);
			return target == null ? Elements.empty() : Elements.singleton(target);
		};
	}

	static <T> Filter<T> identity() {
		return t -> t;
	}

	@SuppressWarnings("unchecked")
	public static <T> Filter<T> ignoreNull() {
		return (Filter<T>) PredicateFilter.IGNORE_NULL;
	}

	@Override
	Elements<E> apply(@NonNull Elements<E> elements);
}
