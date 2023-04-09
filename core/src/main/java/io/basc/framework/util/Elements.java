package io.basc.framework.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
public interface Elements<E> extends Streamable<E>, Iterable<E> {

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
		return (Elements<T>) EmptyElements.INSTANCE;
	}

	public static <T> Elements<T> singleton(@Nullable T element) {
		return of(Collections.singleton(element));
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

		return new SharedElements<>(iterable);
	}

	public static <T> Elements<T> of(Streamable<T> streamable) {
		if (streamable == null) {
			return empty();
		}

		if (streamable instanceof Elements) {
			return (Elements<T>) streamable;
		}

		return new StreamElements<>(streamable);
	}

	@Override
	default <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return new ConvertibleElements<>(this, converter);
	}

	default long count() {
		Stream<E> stream = stream();
		try {
			return stream.count();
		} finally {
			stream.close();
		}
	}

	@Override
	default Elements<E> filter(Predicate<? super E> predicate) {
		return convert((stream) -> stream.filter(predicate));
	}

	@Override
	default <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return convert((stream) -> {
			return stream.flatMap((e) -> {
				Streamable<U> streamy = mapper.apply(e);
				return streamy == null ? Stream.empty() : streamy.stream();
			});
		});
	}

	/**
	 * 默认使用{@link #stream()}的调用
	 * 
	 * @see Stream#forEachOrdered(Consumer)
	 */
	@Override
	default void forEach(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		Stream<E> stream = stream();
		try {
			stream.forEachOrdered(action);
		} finally {
			stream.close();
		}
	}

	default boolean isEmpty() {
		return !findAny().isPresent();
	}

	@Override
	Iterator<E> iterator();

	@Override
	default <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return convert((stream) -> stream.map(mapper));
	}

	/**
	 * Reverses the order of the elements
	 * 
	 * @return
	 */
	default Elements<E> reverse() {
		List<E> list = toList();
		Collections.reverse(list);
		return new ElementList<>(list);
	}
}
