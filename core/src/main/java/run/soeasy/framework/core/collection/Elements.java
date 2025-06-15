package run.soeasy.framework.core.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Pair;

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

	@SuppressWarnings("unchecked")
	public static <T> Elements<T> empty() {
		return (Elements<T>) EmptyElements.EMPTY_ELEMENTS;
	}

	/**
	 * 已知大小的
	 * 
	 * @param statisticsSize
	 * @return
	 */
	default Elements<E> knownSize(@NonNull ToLongFunction<? super Elements<E>> statisticsSize) {
		return new KnownSizeElements<>(this, statisticsSize);
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

	public static <T> Elements<T> forSupplier(@NonNull Supplier<T> supplier) {
		return Elements.of(() -> Stream.generate(supplier));
	}

	public static <T> Elements<T> singleton(T element) {
		return of(Collections.singleton(element));
	}

	/**
	 * 对动态的elements进行缓存
	 * 
	 * @return
	 */
	default Provider<E> cacheable() {
		return new CacheableElements<>(this, Collectors.toList());
	}

	default Elements<E> concat(@NonNull Elements<? extends E> elements) {
		return new MergedElements<>(this, elements);
	}

	/**
	 * 转换
	 * 
	 * @param <U>
	 * @param resize    converter是否可能改变大小
	 * @param converter
	 * @return
	 */
	default <U> Elements<U> map(boolean resize, Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return new ConvertedElements<>(this, resize, converter);
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	default Elements<E> distinct() {
		return map(true, (e) -> e.distinct());
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

	default Elements<E> exclude(@NonNull Predicate<? super E> predicate) {
		return filter(predicate.negate());
	}

	default Elements<E> filter(@NonNull Predicate<? super E> predicate) {
		return map(true, (stream) -> stream.filter(predicate));
	}

	default <U> Elements<U> flatMap(@NonNull Function<? super E, ? extends Streamable<U>> mapper) {
		return map(true, (stream) -> {
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

	default E get(int index) throws IndexOutOfBoundsException {
		return sequential().filter((e) -> e.getIndex() == index).findFirst()
				.orElseThrow(() -> new IndexOutOfBoundsException(String.valueOf(index))).getElement();
	}

	default Elements<Sequential<E>> sequential() {
		return map(false, (stream) -> {
			Stream<Sequential<E>> newStream = CollectionUtils
					.unknownSizeStream(new SequentialIterator<>(stream.iterator()));
			return newStream.onClose(stream::close);
		});
	}

	default <R> Elements<Pair<Sequential<E>, Sequential<R>>> pairs(@NonNull Elements<? extends R> elements) {
		return Elements.of(() -> {
			Stream<E> leftStream = Elements.this.stream();
			Stream<? extends R> rightStream = elements.stream();
			Stream<Pair<Sequential<E>, Sequential<R>>> newStream = CollectionUtils
					.unknownSizeStream(new PairIterator<>(leftStream.iterator(), rightStream.iterator()));
			return newStream.onClose(leftStream::close).onClose(rightStream::close);
		});
	}

	@Override
	Iterator<E> iterator();

	default Elements<E> limit(long maxSize) {
		return map(true, (e) -> e.limit(maxSize));
	}

	default <U> Elements<U> map(@NonNull Function<? super E, ? extends U> mapper) {
		return map(false, (stream) -> stream.map(mapper));
	}

	default Elements<E> peek(@NonNull Consumer<? super E> action) {
		return map(false, (e) -> e.peek(action));
	}

	/**
	 * Reverses the order of the elements
	 * 
	 * @return
	 */
	default Elements<E> reverse() {
		return map(false, (stream) -> stream.sorted(Collections.reverseOrder()));
	}

	default Elements<E> skip(long n) {
		return map(true, (e) -> e.skip(n));
	}

	default Elements<E> sorted() {
		return map(false, (e) -> e.sorted());
	}

	default Elements<E> sorted(@NonNull Comparator<? super E> comparator) {
		return map(false, (e) -> e.sorted(comparator));
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
		return map(false, (e) -> e.unordered());
	}
}
