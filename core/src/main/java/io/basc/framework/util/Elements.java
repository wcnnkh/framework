package io.basc.framework.util;

import java.util.ArrayList;
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
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

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

	@Override
	default Enumeration<E> enumeration() {
		Iterator<E> iterator = iterator();
		if (iterator == null) {
			// 理论上不会为空
			return null;
		}
		return new IteratorToEnumeration<>(iterator, Function.identity());
	}

	/**
	 * @see MultiElements
	 * @param <T>
	 * @param left
	 * @param right
	 * @return
	 */
	public static <T> Elements<T> concat(Elements<? extends T> left, Elements<? extends T> right) {
		Assert.requiredArgument(left != null, "left");
		Assert.requiredArgument(right != null, "right");
		return new MultiElements<>(Arrays.asList(left, right));
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> empty() {
		return (Elements<T>) ElementList.EMPTY;
	}

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> forArray(T... elements) {
		if (elements == null || elements.length == 0) {
			return empty();
		}
		return Elements.of(Arrays.asList(elements));
	}

	public static <T> Elements<T> of(Enumerable<T> enumerable) {
		if (enumerable == null) {
			return empty();
		}

		if (enumerable instanceof Elements) {
			return (Elements<T>) enumerable;
		}

		Iterable<T> iterable = new EnumerableToIterable<>(enumerable, Function.identity());
		return new IterableElements<>(iterable);
	}

	public static <T> Elements<T> of(Iterable<T> iterable) {
		if (iterable == null) {
			return empty();
		}

		if (iterable instanceof Elements) {
			return (Elements<T>) iterable;
		}

		if (iterable instanceof List) {
			return new ElementList<>((List<T>) iterable);
		}

		if (iterable instanceof Set) {
			return new ElementSet<>((Set<T>) iterable);
		}

		if (iterable instanceof Collection) {
			return new ElementCollection<>((Collection<T>) iterable);
		}

		return new IterableElements<>(iterable);
	}

	public static <T> Elements<T> of(Streamable<T> streamable) {
		if (streamable == null) {
			return empty();
		}

		if (streamable instanceof Elements) {
			return (Elements<T>) streamable;
		}

		return new StreamableElements<>(streamable);
	}

	public static <T> Elements<T> singleton(@Nullable T element) {
		return of(Collections.singleton(element));
	}

	/**
	 * 对动态的elements进行缓存
	 * 
	 * @return
	 */
	default Elements<E> cacheable() {
		return new CachedElements<>(this);
	}

	default Elements<E> concat(Elements<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return new MultiElements<>(Arrays.asList(this, elements));
	}

	default <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return new ConvertibleElements<>(this, converter);
	}

	default Elements<E> distinct() {
		return convert((e) -> e.distinct());
	}

	default Elements<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((stream) -> stream.filter(predicate));
	}

	default Elements<E> exclude(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return filter(predicate.negate());
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

	@Override
	default void forEach(Consumer<? super E> action) {
		Streamable.super.forEach(action);
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
		List<E> list = new ArrayList<>();
		iterator().forEachRemaining(list::add);
		if (list.isEmpty()) {
			return empty();
		}
		Collections.reverse(list);
		return new ElementList<>(list);
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
	default ElementList<E> toList() {
		List<E> list = Streamable.super.toList();
		return new ElementList<>(list);
	}

	@Override
	default ElementSet<E> toSet() {
		Set<E> set = Streamable.super.toSet();
		return new ElementSet<>(set);
	}

	default Elements<E> unordered() {
		return convert((e) -> e.unordered());
	}

	default Elements<Indexed<E>> index() {
		return Elements.of(() -> {
			// 在性能中取舍，使用AtomicLong实现，而不使用BigDecimal,使用BigDecimal可以迭代更多的数据
			AtomicLong lineNumber = new AtomicLong();
			return stream().sequential().map((e) -> new Indexed<>(lineNumber.getAndIncrement(), e));
		});
	}
}
