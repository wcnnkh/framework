package io.basc.framework.util.function;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import io.basc.framework.util.Assert;

/**
 * 一个处理者的定义
 * 
 * @author wcnnkh
 *
 * @param <S> 数据来源
 * @param <T> 返回的结果
 * @param <E> 异常
 * @see Function
 */
@FunctionalInterface
public interface Processor<S, T, E extends Throwable> {
	T process(S source) throws E;

	/**
	 * Returns a composed function that first applies the {@code before} function to
	 * its input, and then applies this function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>    the type of input to the {@code before} function, and to the
	 *               composed function
	 * @param before the function to apply before this function is applied
	 * @return a composed function that first applies the {@code before} function
	 *         and then applies this function
	 * @throws NullPointerException if before is null
	 *
	 * @see #andThen(Processor)
	 */
	default <V> Processor<V, T, E> compose(Processor<? super V, ? extends S, ? extends E> before) {
		Objects.requireNonNull(before);
		return (s) -> process(before.process(s));
	}

	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>   the type of output of the {@code after} function, and of the
	 *              composed function
	 * @param after the function to apply after this function is applied
	 * @return a composed function that first applies this function and then applies
	 *         the {@code after} function
	 * @throws NullPointerException if after is null
	 *
	 * @see #compose(Processor)
	 */
	default <V> Processor<S, V, E> andThen(Processor<? super T, ? extends V, ? extends E> after) {
		Objects.requireNonNull(after);
		return (s) -> after.process(process(s));
	}

	/**
	 * Returns a function that always returns its input argument.
	 * 
	 * @param <U>
	 * @param <X>
	 * @return a function that always returns its input argument
	 */
	static <U, X extends Throwable> Processor<U, U, X> identity() {
		return t -> t;
	}

	default <X extends Throwable> Processor<S, T, X> exceptionConvert(
			Processor<? super Throwable, ? extends X, ? extends X> exceptionConverter) {
		return (s) -> {
			try {
				return Processor.this.process(s);
			} catch (Throwable e) {
				throw exceptionConverter.process(e);
			}
		};
	}

	default <TC extends Collection<T>> TC processAll(Iterator<? extends S> sourceIterator, TC targets) throws E {
		if (sourceIterator == null) {
			return targets;
		}

		while (sourceIterator.hasNext()) {
			S source = sourceIterator.next();
			T target = process(source);
			targets.add(target);
		}
		return targets;
	}

	default <TL extends Collection<T>> TL processAll(Iterable<? extends S> sources, TL targets) throws E {
		if (sources == null) {
			return targets;
		}

		return processAll(sources.iterator(), targets);
	}

	default List<T> processAll(Iterator<? extends S> sourceIterator) throws E {
		if (sourceIterator == null) {
			return null;
		}

		if (!sourceIterator.hasNext()) {
			return Collections.emptyList();
		}

		return processAll(sourceIterator, new ArrayList<>());
	}

	default List<T> processAll(Iterable<? extends S> sources) throws E {
		if (sources == null) {
			return null;
		}

		return processAll(sources.iterator());
	}

	default Set<T> processAll(Set<? extends S> sources) throws E {
		if (sources == null) {
			return null;
		}

		if (sources.isEmpty()) {
			return Collections.emptySet();
		}

		return processAll(sources.iterator(), new LinkedHashSet<>(sources.size()));
	}

	@SuppressWarnings("unchecked")
	default T[] processAll(S... sources) throws E {
		if (sources == null) {
			return null;
		}

		Object array = null;
		for (int i = 0; i < sources.length; i++) {
			T target = process(sources[i]);
			if (target != null) {
				if (array == null) {
					array = Array.newInstance(target.getClass(), sources.length);
				}
				Array.set(array, i, target);
			}
		}
		return (T[]) array;
	}

	default T[] processAll(S[] sources, T[] targets) throws E {
		return processAll(sources, 0, targets, 0);
	}

	default T[] processAll(S[] sources, int sourceIndex, T[] targets, int targetIndex) throws E {
		Assert.requiredArgument(sources != null, "sources");
		Assert.requiredArgument(targets != null, "targets");
		for (int i = sourceIndex, insertIndex = targetIndex; sourceIndex < sources.length; i++, insertIndex++) {
			S source = sources[i];
			T target = process(source);
			targets[insertIndex] = target;
		}
		return targets;
	}

	public static <A, B, X extends Throwable> Processor<A, B, X> of(Function<? super A, ? extends B> function) {
		return (s) -> function.apply(s);
	}

	public static <A, B, X extends Throwable, TC extends Collection<B>> TC processAll(
			Iterator<? extends A> sourceIterator, TC targets, Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sourceIterator, targets);
	}

	public static <A, B, X extends Throwable> List<B> processAll(Iterator<? extends A> sourceIterator,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sourceIterator);
	}

	public static <A, B, X extends Throwable> List<B> processAll(Iterable<? extends A> sources,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sources);
	}

	public static <A, B, X extends Throwable> Set<B> processAll(Set<? extends A> sources,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sources);
	}

	public static <A, B, X extends Throwable> B[] processAll(A[] sources,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sources);
	}

	public static <A, B, X extends Throwable> B[] processAll(A[] sources, B[] targets,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sources, targets);
	}

	public static <A, B, X extends Throwable> B[] processAll(A[] sources, int sourceIndex, B[] targets, int targetIndex,
			Processor<? super A, B, ? extends X> processor) throws X {
		return processor.processAll(sources, sourceIndex, targets, targetIndex);
	}
}
