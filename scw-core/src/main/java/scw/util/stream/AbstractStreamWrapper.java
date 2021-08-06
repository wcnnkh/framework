package scw.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import scw.lang.Nullable;
import scw.util.Wrapper;

public abstract class AbstractStreamWrapper<T, M extends Stream<T>, S extends Stream<T>> extends Wrapper<M>
		implements Stream<T> {

	public AbstractStreamWrapper(M stream) {
		super(stream);
	}
	
	public List<T> shared() {
		return collect(Collectors.toList());
	}

	@Nullable
	public T first() {
		return findFirst().orElse(null);
	}

	protected abstract S wrapper(Stream<T> stream);

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return wrappedTarget.spliterator();
	}

	@Override
	public boolean isParallel() {
		return wrappedTarget.isParallel();
	}

	@Override
	public S sequential() {
		Stream<T> stream = this.wrappedTarget.sequential();
		return wrapper(stream);
	}

	@Override
	public S parallel() {
		Stream<T> stream = this.wrappedTarget.parallel();
		return wrapper(stream);
	}

	@Override
	public S unordered() {
		Stream<T> stream = this.wrappedTarget.unordered();
		return wrapper(stream);
	}

	@Override
	public S onClose(Runnable closeHandler) {
		Stream<T> stream = this.wrappedTarget.onClose(closeHandler);
		return wrapper(stream);
	}

	@Override
	public void close() {
		wrappedTarget.close();
	}

	@Override
	public S filter(Predicate<? super T> predicate) {
		Stream<T> stream = this.wrappedTarget.filter(predicate);
		return wrapper(stream);
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return this.wrappedTarget.map(mapper);
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return this.wrappedTarget.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return this.wrappedTarget.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return this.wrappedTarget.mapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return this.wrappedTarget.flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return this.wrappedTarget.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return this.wrappedTarget.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return this.wrappedTarget.flatMapToDouble(mapper);
	}

	@Override
	public S distinct() {
		Stream<T> stream = this.wrappedTarget.distinct();
		return wrapper(stream);
	}

	@Override
	public S sorted() {
		Stream<T> stream = this.wrappedTarget.sorted();
		return wrapper(stream);
	}

	@Override
	public S sorted(Comparator<? super T> comparator) {
		Stream<T> stream = this.wrappedTarget.sorted();
		return wrapper(stream);
	}

	@Override
	public S peek(Consumer<? super T> action) {
		Stream<T> stream = this.wrappedTarget.peek(action);
		return wrapper(stream);
	}

	@Override
	public S limit(long maxSize) {
		Stream<T> stream = this.wrappedTarget.limit(maxSize);
		return wrapper(stream);
	}

	@Override
	public S skip(long n) {
		Stream<T> stream = this.wrappedTarget.skip(n);
		return wrapper(stream);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		this.wrappedTarget.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		this.wrappedTarget.forEachOrdered(action);
	}

	@Override
	public Object[] toArray() {
		return this.wrappedTarget.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return this.wrappedTarget.toArray(generator);
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return this.wrappedTarget.reduce(identity, accumulator);
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		return this.wrappedTarget.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return this.wrappedTarget.reduce(identity, accumulator, combiner);
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return this.wrappedTarget.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return this.wrappedTarget.collect(collector);
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		return this.wrappedTarget.min(comparator);
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		return this.wrappedTarget.max(comparator);
	}

	@Override
	public long count() {
		return this.wrappedTarget.count();
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		return this.wrappedTarget.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		return this.wrappedTarget.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		return this.wrappedTarget.noneMatch(predicate);
	}

	@Override
	public Optional<T> findFirst() {
		return this.wrappedTarget.findFirst();
	}

	@Override
	public Optional<T> findAny() {
		return this.wrappedTarget.findAny();
	}
}