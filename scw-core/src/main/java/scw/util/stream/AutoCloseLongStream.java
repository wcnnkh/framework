package scw.util.stream;

import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

/**
 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对应后未调用任何方法
 * 
 * @author shuchaowen
 * @see AutoCloseLongStreamWrapper
 */
public interface AutoCloseLongStream extends LongStream, AutoCloseBaseStream<Long, LongStream> {
	AutoCloseLongStream filter(LongPredicate predicate);

	AutoCloseLongStream map(LongUnaryOperator mapper);

	<U> AutoCloseStream<U> mapToObj(LongFunction<? extends U> mapper);

	AutoCloseIntStream mapToInt(LongToIntFunction mapper);

	AutoCloseDoubleStream mapToDouble(LongToDoubleFunction mapper);

	AutoCloseLongStream flatMap(LongFunction<? extends LongStream> mapper);

	AutoCloseLongStream distinct();

	AutoCloseLongStream sorted();

	AutoCloseLongStream peek(LongConsumer action);

	AutoCloseLongStream limit(long maxSize);

	AutoCloseLongStream skip(long n);

	AutoCloseLongStream sequential();

	AutoCloseLongStream parallel();

	AutoCloseLongStream unordered();

	AutoCloseLongStream onClose(Runnable closeHandler);

	AutoCloseStream<Long> boxed();
}
