package scw.util.stream;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
 * 
 * @author shuchaowen
 * @see AutoCloseIntStreamWrapper
 *
 */
public interface AutoCloseIntStream extends IntStream, AutoCloseBaseStream<Integer, IntStream> {
	AutoCloseIntStream filter(IntPredicate predicate);

	AutoCloseIntStream map(IntUnaryOperator mapper);

	<U> AutoCloseStream<U> mapToObj(IntFunction<? extends U> mapper);

	AutoCloseLongStream mapToLong(IntToLongFunction mapper);

	AutoCloseDoubleStream mapToDouble(IntToDoubleFunction mapper);

	AutoCloseIntStream flatMap(IntFunction<? extends IntStream> mapper);

	AutoCloseIntStream distinct();

	AutoCloseIntStream sorted();

	AutoCloseIntStream peek(IntConsumer action);

	AutoCloseIntStream limit(long maxSize);

	AutoCloseIntStream skip(long n);

	AutoCloseIntStream sequential();

	AutoCloseIntStream parallel();

	AutoCloseIntStream unordered();

	AutoCloseIntStream onClose(Runnable closeHandler);

	AutoCloseStream<Integer> boxed();
}
