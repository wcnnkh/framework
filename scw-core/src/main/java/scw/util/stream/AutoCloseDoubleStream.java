package scw.util.stream;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

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
