package run.soeasy.framework.util.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.function.Merger;

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

	public static class CacheableElements<E, C extends Collection<E>>
			implements ServiceLoader<E>, CollectionElementsWrapper<E, C>, Serializable {
		private static final long serialVersionUID = 1L;
		private volatile C cached;
		@NonNull
		private final transient Collector<? super E, ?, C> collector;
		@NonNull
		private final transient Streamable<? extends E> streamable;

		public CacheableElements(@NonNull Streamable<? extends E> streamable,
				@NonNull Collector<? super E, ?, C> collector) {
			this.streamable = streamable;
			this.collector = collector;
		}

		@Override
		public ServiceLoader<E> cacheable() {
			return this;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof CacheableElements) {
				CacheableElements<?, ?> other = (CacheableElements<?, ?>) obj;
				return ObjectUtils.equals(getSource(), other.getSource());
			}
			return getSource().equals(obj);
		}

		@Override
		public C getSource() {
			if (cached == null) {
				reload(false);
			}
			return cached;
		}

		@Override
		public int hashCode() {
			return getSource().hashCode();
		}

		@SuppressWarnings("unchecked")
		private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
			this.cached = (C) input.readObject();
		}

		@Override
		public void reload() {
			reload(true);
		}

		public boolean reload(boolean force) {
			if (collector == null || streamable == null) {
				return false;
			}

			if (cached == null || force) {
				synchronized (this) {
					if (cached == null || force) {
						cached = streamable.collect(collector);
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public Stream<E> stream() {
			return getSource().stream();
		}

		@Override
		public String toString() {
			return getSource().toString();
		}

		private void writeObject(ObjectOutputStream output) throws IOException {
			output.writeObject(getSource());
		}
	}

	public static class CollectionElements<E, C extends Collection<E>> extends CacheableElements<E, C> {
		private static final long serialVersionUID = 1L;

		public CollectionElements(@NonNull Streamable<E> streamable, @NonNull Collector<? super E, ?, C> collector) {
			super(streamable, collector);
			reload(true);
		}

		@Override
		public Iterator<E> iterator() {
			return getSource().iterator();
		}

		@Override
		public int size() {
			return getSource().size();
		}
	}

	public static interface CollectionElementsWrapper<E, W extends Collection<E>>
			extends CollectionWrapper<E, W>, IterableElementsWrapper<E, W> {

		@Override
		default boolean contains(Object element) {
			return CollectionWrapper.super.contains(element);
		}

		@Override
		default boolean isEmpty() {
			return CollectionWrapper.super.isEmpty();
		}

		@Override
		default boolean isUnique() {
			return size() == 1;
		}

		@Override
		default Iterator<E> iterator() {
			return CollectionWrapper.super.iterator();
		}

		@Override
		default Stream<E> stream() {
			return CollectionWrapper.super.stream();
		}

		@Override
		default Object[] toArray() {
			return CollectionWrapper.super.toArray();
		}

		@Override
		default <T> T[] toArray(T[] array) {
			return CollectionWrapper.super.toArray(array);
		}

	}

	@RequiredArgsConstructor
	@Getter
	public static class ConvertedElements<S, E, W extends Elements<S>> implements ElementsWrapper<E, Elements<E>> {
		@NonNull
		private final W target;
		@NonNull
		private final Function<? super Stream<S>, ? extends Stream<E>> converter;

		@Override
		public Elements<E> getSource() {
			return Elements.of(() -> converter.apply(target.stream()));
		}
	}

	public static interface ElementsWrapper<E, W extends Elements<E>> extends Elements<E>, StreamableWrapper<E, W> {

		@Override
		default ServiceLoader<E> cacheable() {
			return getSource().cacheable();
		}

		@Override
		default Elements<E> concat(Elements<? extends E> elements) {
			return getSource().concat(elements);
		}

		@Override
		default <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
			return getSource().convert(converter);
		}

		@Override
		default Elements<E> distinct() {
			return getSource().distinct();
		}

		@Override
		default Enumeration<E> enumeration() {
			return getSource().enumeration();
		}

		@Override
		default Elements<E> exclude(Predicate<? super E> predicate) {
			return getSource().exclude(predicate);
		}

		@Override
		default Elements<E> filter(Predicate<? super E> predicate) {
			return getSource().filter(predicate);
		}

		@Override
		default <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
			return getSource().flatMap(mapper);
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			getSource().forEach(action);
		}

		@Override
		default Optional<Indexed<E>> index(long index) {
			return getSource().index(index);
		}

		@Override
		default Elements<Indexed<E>> indexed() {
			return getSource().indexed();
		}

		@Override
		default Elements<IterativeElement<E>> iterative() {
			return getSource().iterative();
		}

		@Override
		default Iterator<E> iterator() {
			return getSource().iterator();
		}

		@Override
		default Elements<E> limit(long maxSize) {
			return getSource().limit(maxSize);
		}

		@Override
		default <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default Elements<E> reverse() {
			return getSource().reverse();
		}

		@Override
		default Elements<E> skip(long n) {
			return getSource().skip(n);
		}

		@Override
		default Elements<E> sorted() {
			return getSource().sorted();
		}

		@Override
		default Elements<E> sorted(Comparator<? super E> comparator) {
			return getSource().sorted(comparator);
		}

		@Override
		default Spliterator<E> spliterator() {
			return getSource().spliterator();
		}

		@Override
		default ListElementsWrapper<E, ? extends List<E>> toList() {
			return getSource().toList();
		}

		@Override
		default SetElementsWrapper<E, ? extends Set<E>> toSet() {
			return getSource().toSet();
		}

		@Override
		default Elements<E> unordered() {
			return getSource().unordered();
		}
	}

	public static class EmptyElements<E> extends EmptyStreamable<E> implements Elements<E> {
		private static final long serialVersionUID = 1L;

		@Override
		public ServiceLoader<E> cacheable() {
			return ServiceLoader.empty();
		}

		@Override
		public Elements<E> clone() {
			return this;
		}

		@Override
		public Elements<E> filter(Predicate<? super E> predicate) {
			return this;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Iterator<E> iterator() {
			return Collections.emptyIterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
			return (Elements<U>) this;
		}

		@Override
		public Elements<E> reverse() {
			return this;
		}

		@Override
		public Stream<E> stream() {
			return Stream.empty();
		}

		@Override
		public ListElementsWrapper<E, ?> toList() {
			return new StandardListElements<>(Collections.emptyList());
		}

		@Override
		public SetElementsWrapper<E, ?> toSet() {
			return new StandardSetElements<>(Collections.emptySet());
		}
	}

	@Data
	@EqualsAndHashCode(of = "element")
	@AllArgsConstructor
	public static final class Indexed<E> implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * 索引，从0开始
		 */
		private final long index;
		/**
		 * 对应的元素
		 */
		private final E element;
	}

	public static interface IterableElementsWrapper<E, W extends Iterable<E>>
			extends Elements<E>, IterableWrapper<E, W> {

		@Override
		default void forEach(Consumer<? super E> action) {
			Elements.super.forEach(action);
		}

		@Override
		default Iterator<E> iterator() {
			return IterableWrapper.super.iterator();
		}

		@Override
		default Stream<E> stream() {
			return Streams.stream(spliterator());
		}
	}

	@Data
	public static class IterativeElement<T> implements Serializable {
		private static final long serialVersionUID = 1L;
		private final T value;
		private final boolean last;
	}

	@AllArgsConstructor
	public static class IterativeElementIterator<E> implements Iterator<IterativeElement<E>> {
		@NonNull
		private final Iterator<E> iterator;

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public IterativeElement<E> next() {
			if (!iterator.hasNext()) {
				throw new NoSuchElementException();
			}

			E value = iterator.next();
			return new IterativeElement<>(value, !iterator.hasNext());
		}

	}

	@AllArgsConstructor
	public static class IterativeElements<E> implements Elements<IterativeElement<E>>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private final Elements<E> source;

		@Override
		public Iterator<IterativeElement<E>> iterator() {
			return new IterativeElementIterator<>(source.iterator());
		}

		@Override
		public Stream<IterativeElement<E>> stream() {
			return Streams.stream(iterator());
		}
	}

	public static class ListElements<E> extends CollectionElements<E, List<E>>
			implements ListElementsWrapper<E, List<E>> {
		private static final long serialVersionUID = 1L;

		public ListElements(Elements<E> elements) {
			super(elements, Collectors.toList());
		}
	}

	public static interface ListElementsWrapper<E, W extends List<E>>
			extends ListWrapper<E, W>, CollectionElementsWrapper<E, W> {

		@Override
		default boolean contains(Object o) {
			return ListWrapper.super.contains(o);
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			ListWrapper.super.forEach(action);
		}

		@Override
		default E get(int index) throws IndexOutOfBoundsException {
			List<E> list = getSource();
			return list.get((int) index);
		}

		@Override
		default E getUnique() throws NoSuchElementException, NoUniqueElementException {
			List<E> list = getSource();
			if (list.isEmpty()) {
				throw new NoSuchElementException();
			}

			if (list.size() != 1) {
				throw new NoUniqueElementException();
			}
			return list.get(0);
		}

		@Override
		default Optional<Indexed<E>> index(long index) {
			if (index > Integer.MAX_VALUE) {
				return Optional.empty();
			}

			E value = get((int) index);
			return Optional.of(new Indexed<>(index, value));
		}

		@Override
		default boolean isEmpty() {
			return ListWrapper.super.isEmpty();
		}

		@Override
		default Iterator<E> iterator() {
			return ListWrapper.super.iterator();
		}

		@Override
		default Elements<E> reverse() {
			return Elements.of(() -> CollectionUtils.getIterator(getSource(), true));
		}

		@Override
		default Stream<E> stream() {
			return ListWrapper.super.stream();
		}

		@Override
		default Object[] toArray() {
			return ListWrapper.super.toArray();
		}

		@Override
		default <T> T[] toArray(T[] array) {
			return CollectionElementsWrapper.super.toArray(array);
		}

		@Override
		default ListElementsWrapper<E, W> toList() {
			return this;
		}

	}

	public static interface ListWrapper<E, W extends List<E>> extends List<E>, CollectionWrapper<E, W> {

		@Override
		default boolean add(E e) {
			return getSource().add(e);
		}

		@Override
		default void add(int index, E element) {
			getSource().add(index, element);
		}

		@Override
		default boolean addAll(Collection<? extends E> c) {
			return getSource().addAll(c);
		}

		@Override
		default boolean addAll(int index, Collection<? extends E> c) {
			return getSource().addAll(index, c);
		}

		@Override
		default void clear() {
			getSource().clear();
		}

		@Override
		default boolean contains(Object o) {
			return getSource().contains(o);
		}

		@Override
		default boolean containsAll(Collection<?> c) {
			return getSource().containsAll(c);
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			getSource().forEach(action);
		}

		@Override
		default E get(int index) {
			return getSource().get(index);
		}

		@Override
		default int indexOf(Object o) {
			return getSource().indexOf(o);
		}

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default Iterator<E> iterator() {
			return getSource().iterator();
		}

		@Override
		default int lastIndexOf(Object o) {
			return getSource().lastIndexOf(o);
		}

		@Override
		default ListIterator<E> listIterator() {
			return getSource().listIterator();
		}

		@Override
		default ListIterator<E> listIterator(int index) {
			return getSource().listIterator(index);
		}

		@Override
		default Stream<E> parallelStream() {
			return getSource().parallelStream();
		}

		@Override
		default E remove(int index) {
			return getSource().remove(index);
		}

		@Override
		default boolean remove(Object o) {
			return getSource().remove(o);
		}

		@Override
		default boolean removeAll(Collection<?> c) {
			return getSource().removeAll(c);
		}

		@Override
		default boolean removeIf(Predicate<? super E> filter) {
			return getSource().removeIf(filter);
		}

		@Override
		default void replaceAll(UnaryOperator<E> operator) {
			getSource().replaceAll(operator);
		}

		@Override
		default boolean retainAll(Collection<?> c) {
			return getSource().retainAll(c);
		}

		@Override
		default E set(int index, E element) {
			return getSource().set(index, element);
		}

		@Override
		default int size() {
			return getSource().size();
		}

		@Override
		default void sort(Comparator<? super E> c) {
			getSource().sort(c);
		}

		@Override
		default Spliterator<E> spliterator() {
			return getSource().spliterator();
		}

		@Override
		default Stream<E> stream() {
			return getSource().stream();
		}

		@Override
		default List<E> subList(int fromIndex, int toIndex) {
			return getSource().subList(fromIndex, toIndex);
		}

		@Override
		default Object[] toArray() {
			return getSource().toArray();
		}

		@Override
		default <T> T[] toArray(T[] a) {
			return getSource().toArray(a);
		}
	}

	public static class MergedElements<E> implements ElementsWrapper<E, Elements<E>> {
		private static final int JOIN_MAX_LENGTH = Integer.max(4,
				Integer.getInteger(MergedElements.class.getName() + ".maxLength", 256));
		private final Elements<? extends E>[] members;
		private final Merger<Elements<E>> merger;

		@SafeVarargs
		public MergedElements(Elements<? extends E>... members) {
			this(Merger.flat(), members);
		}

		@SafeVarargs
		public MergedElements(Merger<Elements<E>> merger, Elements<? extends E>... members) {
			Assert.requiredArgument(merger != null, "merger");
			Assert.requiredArgument(members != null, "members");
			this.members = members;
			this.merger = merger;
		}

		@Override
		public Elements<E> concat(Elements<? extends E> elements) {
			if (members.length == JOIN_MAX_LENGTH) {
				// 如果数组已经达到最大值那么使用嵌套方式实现
				return new MergedElements<>(merger, this, elements);
			} else {
				// 使用数组实现，用来解决大量嵌套对象问题
				Elements<? extends E>[] newMembers = Arrays.copyOf(members, members.length + 1);
				newMembers[members.length] = elements;
				return new MergedElements<>(merger, newMembers);
			}
		}

		@Override
		public Elements<E> getSource() {
			Elements<Elements<? extends E>> members = Elements.forArray(this.members);
			return merger.apply(members.map((elements) -> elements.map(Function.identity())));
		}
	}

	@Data
	public static class ParallelElement<L, R> implements Serializable {
		private static final long serialVersionUID = 1L;
		private final IterativeElement<L> left;
		private final IterativeElement<R> right;

		public L getLeftValue() {
			return left == null ? null : left.getValue();
		}

		public R getRightValue() {
			return right == null ? null : right.getValue();
		}

		/**
		 * 并行分支是否都存在
		 * 
		 * @return
		 */
		public boolean isPresent() {
			return left != null && right != null;
		}
	}

	@RequiredArgsConstructor
	public static class ParallelElementIterator<L, R> implements Iterator<ParallelElement<L, R>> {
		@NonNull
		private final Iterator<? extends L> leftIterator;
		@NonNull
		private final Iterator<? extends R> rightIterator;

		@Override
		public boolean hasNext() {
			return leftIterator.hasNext() || rightIterator.hasNext();
		}

		@Override
		public ParallelElement<L, R> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			IterativeElement<L> left = leftIterator.hasNext()
					? new IterativeElement<>(leftIterator.next(), !leftIterator.hasNext())
					: null;
			IterativeElement<R> right = rightIterator.hasNext()
					? new IterativeElement<>(rightIterator.next(), !rightIterator.hasNext())
					: null;
			return new ParallelElement<>(left, right);
		}
	}

	@Data
	public static class ParallelElements<L, R> implements Elements<ParallelElement<L, R>>, Serializable {
		private static final long serialVersionUID = 1L;
		private final Elements<? extends L> leftElements;
		private final Elements<? extends R> rightElements;

		@Override
		public Iterator<ParallelElement<L, R>> iterator() {
			return new ParallelElementIterator<>(leftElements.iterator(), rightElements.iterator());
		}

		@Override
		public Stream<ParallelElement<L, R>> stream() {
			return Streams.stream(iterator());
		}
	}

	public static class SetElements<E> extends CollectionElements<E, Set<E>> implements SetElementsWrapper<E, Set<E>> {
		private static final long serialVersionUID = 1L;

		public SetElements(@NonNull Elements<E> elements) {
			super(elements, Collectors.toSet());
		}
	}

	public static interface SetElementsWrapper<E, W extends Set<E>>
			extends SetWrapper<E, W>, CollectionElementsWrapper<E, W> {

		@Override
		default boolean contains(Object o) {
			return SetWrapper.super.contains(o);
		}

		@Override
		default Elements<E> distinct() {
			return this;
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			SetWrapper.super.forEach(action);
		}

		@Override
		default boolean isEmpty() {
			return SetWrapper.super.isEmpty();
		}

		@Override
		default Iterator<E> iterator() {
			return SetWrapper.super.iterator();
		}

		@Override
		default Stream<E> stream() {
			return SetWrapper.super.stream();
		}

		@Override
		default Object[] toArray() {
			return SetWrapper.super.toArray();
		}

		@Override
		default <T> T[] toArray(T[] a) {
			return SetWrapper.super.toArray(a);
		}

		@Override
		default SetElementsWrapper<E, W> toSet() {
			return this;
		}
	}

	public static interface SetWrapper<E, W extends Set<E>> extends Set<E>, CollectionWrapper<E, W> {

		@Override
		default boolean add(E e) {
			return getSource().add(e);
		}

		@Override
		default boolean addAll(Collection<? extends E> c) {
			return getSource().addAll(c);
		}

		@Override
		default void clear() {
			getSource().clear();
		}

		@Override
		default boolean contains(Object o) {
			return getSource().contains(o);
		}

		@Override
		default boolean containsAll(Collection<?> c) {
			return getSource().containsAll(c);
		}

		@Override
		default void forEach(Consumer<? super E> action) {
			getSource().forEach(action);
		}

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default Iterator<E> iterator() {
			return getSource().iterator();
		}

		@Override
		default Stream<E> parallelStream() {
			return getSource().parallelStream();
		}

		@Override
		default boolean remove(Object o) {
			return getSource().remove(o);
		}

		@Override
		default boolean removeAll(Collection<?> c) {
			return getSource().removeAll(c);
		}

		@Override
		default boolean removeIf(Predicate<? super E> filter) {
			return getSource().removeIf(filter);
		}

		@Override
		default boolean retainAll(Collection<?> c) {
			return getSource().retainAll(c);
		}

		@Override
		default int size() {
			return getSource().size();
		}

		@Override
		default Spliterator<E> spliterator() {
			return getSource().spliterator();
		}

		@Override
		default Stream<E> stream() {
			return getSource().stream();
		}

		@Override
		default Object[] toArray() {
			return getSource().toArray();
		}

		@Override
		default <T> T[] toArray(T[] a) {
			return getSource().toArray(a);
		}
	}

	public static class StandardCollectionElements<E, W extends Collection<E>> extends StandardIterableElements<E, W>
			implements CollectionElementsWrapper<E, W> {
		private static final long serialVersionUID = 1L;

		public StandardCollectionElements(@NonNull W source) {
			super(source);
		}

	}

	@EqualsAndHashCode
	@ToString
	@RequiredArgsConstructor
	@Getter
	public static class StandardIterableElements<E, W extends Iterable<E>>
			implements IterableElementsWrapper<E, W>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private W source;
	}

	public class StandardListElements<E, W extends List<E>> extends StandardCollectionElements<E, W>
			implements ListElementsWrapper<E, W> {
		private static final long serialVersionUID = 1L;

		public StandardListElements(@NonNull W source) {
			super(source);
		}
	}

	public static class StandardSetElements<E, W extends Set<E>> extends StandardCollectionElements<E, W>
			implements SetElementsWrapper<E, W> {
		private static final long serialVersionUID = 1L;

		public StandardSetElements(@NonNull W source) {
			super(source);
		}

	}

	@RequiredArgsConstructor
	@Getter
	public static class StandardStreamableElements<E, W extends Streamable<E>>
			implements StreamableElementsWrapper<E, W> {
		private final W source;

		@Override
		public Iterator<E> iterator() {
			List<E> list = source.collect(Collectors.toList());
			return list.iterator();
		}

		@Override
		public Stream<E> stream() {
			return source.stream();
		}
	}

	public static interface StreamableElementsWrapper<E, W extends Streamable<E>>
			extends StreamableWrapper<E, W>, Elements<E> {

		@Override
		default void forEach(Consumer<? super E> action) {
			Elements.super.forEach(action);
		}

		@Override
		default ListElementsWrapper<E, ?> toList() {
			return Elements.super.toList();
		}

		@Override
		default SetElementsWrapper<E, ?> toSet() {
			return Elements.super.toSet();
		}
	}

	public static final EmptyElements<Object> EMPTY_ELEMENTS = new EmptyElements<>();

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
		return new ConvertedElements<>(this, converter);
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

	default Optional<Indexed<E>> index(long index) {
		Indexed<E> indexed = index == 0 ? indexed().first() : indexed().filter((e) -> e.getIndex() == index).first();
		if (indexed == null) {
			return Optional.empty();
		}
		return Optional.of(indexed);
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
}
