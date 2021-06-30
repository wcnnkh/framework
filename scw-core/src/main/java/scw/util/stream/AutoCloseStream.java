package scw.util.stream;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import scw.lang.Nullable;

/**
 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法<br/>
 * 一般来说只可以迭代一次
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @see AutoCloseStreamWrapper
 */
public interface AutoCloseStream<T> extends Stream<T>, AutoCloseBaseStream<T, Stream<T>> {
	default List<T> shared() {
		return collect(Collectors.toList());
	}

	@Nullable
	default T first() {
		return findFirst().orElse(null);
	}

	AutoCloseStream<T> filter(Predicate<? super T> predicate);

	<R> AutoCloseStream<R> map(Function<? super T, ? extends R> mapper);

	AutoCloseIntStream mapToInt(ToIntFunction<? super T> mapper);

	AutoCloseLongStream mapToLong(ToLongFunction<? super T> mapper);

	AutoCloseDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

	<R> AutoCloseStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

	AutoCloseIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

	AutoCloseLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

	AutoCloseDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

	AutoCloseStream<T> distinct();

	AutoCloseStream<T> sorted();

	AutoCloseStream<T> sorted(Comparator<? super T> comparator);

	AutoCloseStream<T> peek(Consumer<? super T> action);

	AutoCloseStream<T> limit(long maxSize);

	AutoCloseStream<T> skip(long n);

	AutoCloseStream<T> sequential();

	AutoCloseStream<T> parallel();

	AutoCloseStream<T> unordered();

	AutoCloseStream<T> onClose(Runnable closeHandler);
}
