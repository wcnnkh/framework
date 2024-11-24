package io.basc.framework.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 和{@link Streamable}类似，但此接口可以返回无需关闭的{@link Iterator}
 * <p>
 * 一般用于代替{@link Collection}返回，可以支持一次性加载到内存和部分加载到内存
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface Elements<E> extends Streamable<E>, Iterable<E>, Enumerable<E> {
	static final Elements<?> EMPTY_ELEMENTS = new StandardListElements<>(Collections.emptyList());

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> empty() {
		return (Elements<T>) EMPTY_ELEMENTS;
	}

	@SafeVarargs
	public static <T> Elements<T> forArray(T... elements) {
		if (elements == null || elements.length == 0) {
			return empty();
		}
		return Elements.of(Arrays.asList(elements));
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> of(Enumerable<? extends T> enumerable) {
		if (enumerable == null) {
			return empty();
		}

		if (enumerable instanceof Elements) {
			return (Elements<T>) enumerable;
		}

		Iterable<T> iterable = new EnumerableToIterable<>(enumerable, Function.identity());
		return new StandardIterableElements<>(iterable);
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> of(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return empty();
		}

		if (iterable instanceof Elements) {
			return (Elements<T>) iterable;
		}

		if (iterable instanceof List) {
			return new StandardListElements<>((List<T>) iterable);
		}

		if (iterable instanceof Set) {
			return new StandardSetElements<>((Set<T>) iterable);
		}

		if (iterable instanceof Collection) {
			return new StandardCollectionElements<>((Collection<T>) iterable);
		}
		// 不管为啥,强行转换
		return new StandardIterableElements<>((Iterable<T>) iterable);
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> of(Streamable<? extends T> streamable) {
		if (streamable == null) {
			return empty();
		}

		if (streamable instanceof Elements) {
			return (Elements<T>) streamable;
		}

		return new StandardStreamableElements<>((Streamable<T>) streamable);
	}

	public static <T> Elements<T> singleton(T element) {
		return of(Collections.singleton(element));
	}

	/**
	 * 对动态的elements进行缓存
	 * 
	 * @return
	 */
	default ServiceLoader<E> cacheable() {
		return new CacheableElements<>(this, Collectors.toList());
	}

	default Elements<E> concat(Elements<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return new MergedElements<>(this, elements);
	}

	default <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return new ConvertibleElements<>(this, converter);
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	default Elements<E> distinct() {
		return convert((e) -> e.distinct());
	}

	@Override
	default Enumeration<E> enumeration() {
		Iterator<E> iterator = iterator();
		if (iterator == null) {
			// 理论上不会为空
			return null;
		}
		return new IteratorToEnumeration<>(iterator, Function.identity());
	}

	default Elements<E> exclude(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return filter(predicate.negate());
	}

	default Elements<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((stream) -> stream.filter(predicate));
	}

	default <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((stream) -> {
			return stream.flatMap((e) -> {
				Streamable<U> streamy = mapper.apply(e);
				return streamy == null ? Stream.empty() : streamy.stream();
			});
		});
	}

	/**
	 * 默认使用{@link Streamable#forEach(Consumer)}, 因为迭代的大多数实现都是一次性加载到内容
	 */
	@Override
	default void forEach(Consumer<? super E> action) {
		Streamable.super.forEach(action);
	}

	default Elements<Indexed<E>> indexed() {
		return Elements.of(() -> {
			AtomicLong lineNumber = new AtomicLong();
			return stream().sequential().map((e) -> new Indexed<>(lineNumber.getAndIncrement(), e));
		});
	}

	default Elements<IterativeElement<E>> iterative() {
		return new IterativeElements<>(this);
	}

	@Override
	Iterator<E> iterator();

	default Elements<E> limit(long maxSize) {
		return convert((e) -> e.limit(maxSize));
	}

	default <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((stream) -> stream.map(mapper));
	}

	default Elements<E> parallel() {
		return convert((e) -> e.parallel());
	}

	default <R> Elements<ParallelElement<E, R>> parallel(Elements<? extends R> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return new ParallelElements<>(this, elements);
	}

	default Elements<E> peek(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		return convert((e) -> e.peek(action));
	}

	/**
	 * Reverses the order of the elements
	 * 
	 * @return
	 */
	default Elements<E> reverse() {
		return convert((stream) -> stream.sorted(Collections.reverseOrder()));
	}

	default Elements<E> sequential() {
		return convert((e) -> e.sequential());
	}

	default Elements<E> skip(long n) {
		return convert((e) -> e.skip(n));
	}

	default Elements<E> sorted() {
		return convert((e) -> e.sorted());
	}

	default Elements<E> sorted(Comparator<? super E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		return convert((e) -> e.sorted(comparator));
	}

	@Override
	default ListElementsWrapper<E, ?> toList() {
		return new ListElements<>(this);
	}

	@Override
	default SetElementsWrapper<E, ?> toSet() {
		return new SetElements<>(this);
	}

	default Elements<E> unordered() {
		return convert((e) -> e.unordered());
	}

	default E index(long index) throws IndexOutOfBoundsException {
		Indexed<E> indexed = index == 0 ? indexed().first() : indexed().filter((e) -> e.getIndex() == index).first();
		if (indexed == null) {
			throw new IndexOutOfBoundsException("index out of range: " + index);
		}
		return indexed.getElement();
	}
}
