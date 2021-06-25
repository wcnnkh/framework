package scw.util.stream;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

/**
 * 虽然可以自动关闭，并并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
 * 
 * @author shuchaowen
 * @see AutoCloseDoubleStreamWrapper
 */
public interface AutoCloseDoubleStream extends DoubleStream, AutoCloseBaseStream<Double, DoubleStream> {

	AutoCloseDoubleStream filter(DoublePredicate predicate);

	AutoCloseDoubleStream map(DoubleUnaryOperator mapper);

	<U> AutoCloseStream<U> mapToObj(DoubleFunction<? extends U> mapper);

	AutoCloseIntStream mapToInt(DoubleToIntFunction mapper);

	AutoCloseLongStream mapToLong(DoubleToLongFunction mapper);

	AutoCloseDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper);

	AutoCloseDoubleStream distinct();

	AutoCloseDoubleStream sorted();

	AutoCloseDoubleStream peek(DoubleConsumer action);

	AutoCloseDoubleStream limit(long maxSize);

	AutoCloseDoubleStream skip(long n);

	AutoCloseDoubleStream sequential();

	AutoCloseDoubleStream parallel();

	AutoCloseDoubleStream unordered();

	AutoCloseDoubleStream onClose(Runnable closeHandler);

	AutoCloseStream<Double> boxed();
}
