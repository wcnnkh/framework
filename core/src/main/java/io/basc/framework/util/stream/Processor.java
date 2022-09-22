package io.basc.framework.util.stream;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;

/**
 * 一个处理者的定义
 * 
 * @author shuchaowen
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
	 * 在执行外部嵌套一个执行器
	 * 
	 * @param <B>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <B> Processor<B, T, E> beforeProcess(Processor<B, ? extends S, ? extends E> processor,
			@Nullable ConsumerProcessor<S, ? extends E> closeProcessor) {
		return new NestingProcessor<B, S, T, E>(processor, this, closeProcessor);
	}

	/**
	 * 在执行内部嵌套一个执行器
	 * 
	 * @param <A>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <A> Processor<S, A, E> afterProcess(Processor<T, ? extends A, ? extends E> processor,
			@Nullable ConsumerProcessor<T, ? extends E> closeProcessor) {
		return new NestingProcessor<S, T, A, E>(this, processor, closeProcessor);
	}

	default <X extends Throwable> Processor<S, T, X> exceptionConvert(Processor<Throwable, X, X> exceptionConverter) {
		return new Processor<S, T, X>() {

			@Override
			public T process(S source) throws X {
				try {
					return Processor.this.process(source);
				} catch (Throwable e) {
					throw exceptionConverter.process(e);
				}
			}
		};
	}

	default <TL extends Collection<T>> TL processTo(Collection<? extends S> sourceList, TL targetList) throws E {
		if (sourceList == null) {
			return targetList;
		}

		for (S source : sourceList) {
			T target = process(source);
			targetList.add(target);
		}
		return targetList;
	}

	@SuppressWarnings("unchecked")
	default <TL extends Collection<T>> TL processAll(Collection<? extends S> sources) throws E {
		if (sources == null) {
			return null;
		}

		if (sources.isEmpty()) {
			return CollectionFactory.empty(sources.getClass());
		}

		TL collection = (TL) CollectionFactory.createCollection(sources.getClass(),
				CollectionFactory.getEnumSetElementType(sources), sources.size());
		for (S source : sources) {
			T target = process(source);
			collection.add(target);
		}
		return collection;
	}

	@Nullable
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

	default void processTo(S[] sources, T[] targets) throws E {
		processTo(sources, 0, targets, 0);
	}

	default void processTo(S[] sources, int sourceIndex, T[] targets, int targetIndex) throws E {
		Assert.requiredArgument(sources != null, "sources");
		Assert.requiredArgument(targets != null, "targets");

		for (int i = sourceIndex, insertIndex = targetIndex; sourceIndex < sources.length; i++, insertIndex++) {
			S source = sources[i];
			T target = process(source);
			targets[insertIndex] = target;
		}
	}

	public static <R, E extends Throwable> Processor<R, R, E> identity() {
		return e -> e;
	}
}
