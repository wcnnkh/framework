package scw.util.stream;

import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

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
