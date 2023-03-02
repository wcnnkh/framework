package io.basc.framework.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 一个回调的定义
 * 
 * @see Consumer
 * @author wcnnkh
 *
 * @param <S> 回调的数据类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface ConsumeProcessor<S, E extends Throwable> {
	void process(S source) throws E;

	/**
	 * Returns a composed function that first applies the {@code before} function to
	 * its input, and then applies this function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param before the function to apply before this function is applied
	 * @throws NullPointerException if before is null
	 * @see #andThen(ConsumeProcessor)
	 */
	default ConsumeProcessor<S, E> compose(ConsumeProcessor<? super S, ? extends E> before) {
		Objects.requireNonNull(before);
		return (t) -> {
			before.process(t);
			process(t);
		};
	}

	/**
	 * Returns a composed {@code Consumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the composed
	 * operation. If performing this operation throws an exception, the
	 * {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code Consumer} that performs in sequence this operation
	 *         followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default ConsumeProcessor<S, E> andThen(ConsumeProcessor<? super S, ? extends E> after) {
		Objects.requireNonNull(after);
		return (t) -> {
			process(t);
			after.process(t);
		};
	}

	default Processor<S, Void, E> toProcessor() {
		return new Processor<S, Void, E>() {

			@Override
			public Void process(S source) throws E {
				ConsumeProcessor.this.process(source);
				return null;
			}
		};
	}

	/**
	 * 即使出现异常也会将执行其他
	 * 
	 * @param sourceIterator
	 * @throws E
	 */
	default void processAll(Iterator<? extends S> sourceIterator) throws E {
		Assert.requiredArgument(sourceIterator != null, "sourceIterator");
		if (sourceIterator.hasNext()) {
			S source = sourceIterator.next();
			try {
				process(source);
			} finally {
				processAll(sourceIterator);
			}
		}
	}

	default void processAll(Iterable<? extends S> sources) throws E {
		Assert.requiredArgument(sources != null, "sources");
		processAll(sources.iterator());
	}

	/**
	 * 即使出现异常也会将执行其他
	 * 
	 * @param sourceIterator
	 * @throws X
	 */
	public static <A, X extends Throwable> void consumeAll(Iterator<? extends A> sourceIterator,
			ConsumeProcessor<? super A, ? extends X> processor) throws X {
		Assert.requiredArgument(processor != null, "processor");
		processor.processAll(sourceIterator);
	}

	public static <A, X extends Throwable> void consumeAll(Iterable<? extends A> sources,
			ConsumeProcessor<? super A, ? extends X> processor) throws X {
		Assert.requiredArgument(processor != null, "processor");
		processor.processAll(sources);
	}
}
