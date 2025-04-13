package run.soeasy.framework.core.function;

import java.util.Iterator;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.collection.Streamable;

/**
 * 一个终点的定义
 * 
 * @author wcnnkh
 *
 * @param <S> 回调的数据类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface Consumer<S, E extends Throwable> {
	@FunctionalInterface
	public interface ConsumerWrapper<S, E extends Throwable, W extends Consumer<S, E>>
			extends Consumer<S, E>, Wrapper<W> {
		@Override
		default void accept(S source) throws E {
			getSource().accept(source);
		}

		@Override
		default <T> Consumer<T, E> map(@NonNull Function<? super T, ? extends S, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedConsumer<S, T, E extends Throwable, W extends Consumer<T, E>>
			implements Consumer<S, E>, Wrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super S, ? extends T, ? extends E> mapper;

		@Override
		public void accept(S source) throws E {
			T target = mapper.apply(source);
			this.source.accept(target);
		}
	}

	@RequiredArgsConstructor
	public static class AndThenConsumer<S, E extends Throwable> implements Consumer<S, E> {
		@NonNull
		private final Consumer<? super S, ? extends E> left;
		@NonNull
		private final Consumer<? super S, ? extends E> right;

		@Override
		public void accept(S source) throws E {
			left.accept(source);
			right.accept(source);
		}
	}

	@RequiredArgsConstructor
	public static class OnCloseConsumer<S, E extends Throwable> implements Consumer<S, E> {
		@NonNull
		private final Consumer<? super S, ? extends E> left;
		@NonNull
		private final Consumer<? super S, ? extends E> right;

		@Override
		public void accept(S source) throws E {
			try {
				left.accept(source);
			} finally {
				right.accept(source);
			}
		}
	}

	/**
	 * 拒绝，不做任何操作
	 * 
	 * @author shuchaowen
	 *
	 * @param <T>
	 * @param <E>
	 */
	public static class RejectConsumer<A, B extends Throwable> implements Consumer<A, B> {

		@Override
		public void accept(A source) throws B {
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Consumer<T, B> map(@NonNull Function<? super T, ? extends A, ? extends B> mapper) {
			return (Consumer<T, B>) REJECT_CONSUMER;
		}
	}

	public static final RejectConsumer<?, ?> REJECT_CONSUMER = new RejectConsumer<>();

	public static class NativeConsumer<S, E extends Throwable> extends Wrapped<java.util.function.Consumer<? super S>>
			implements Consumer<S, E> {

		public NativeConsumer(java.util.function.Consumer<? super S> source) {
			super(source);
		}

		@Override
		public void accept(S source) throws E {
			this.source.accept(source);
		}
	}

	public static <S, E extends Throwable> Consumer<S, E> forNative(
			@NonNull java.util.function.Consumer<? super S> consumer) {
		return new NativeConsumer<>(consumer);
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Consumer<T, E> reject() {
		return (Consumer<T, E>) REJECT_CONSUMER;
	}

	void accept(S source) throws E;

	public static <S, E extends Throwable> void acceptAll(@NonNull Streamable<? extends S> streamable,
			@NonNull Consumer<? super S, ? extends E> endpoint) throws E {
		Stream<? extends S> stream = streamable.stream();
		try {
			endpoint.acceptAll(stream.iterator());
		} finally {
			stream.close();
		}
	}

	public static <S, E extends Throwable> void acceptAll(@NonNull Iterator<? extends S> iterator,
			@NonNull Consumer<? super S, ? extends E> endpoint) throws E {
		endpoint.acceptAll(iterator);
	}

	default void acceptAll(@NonNull Iterator<? extends S> iterator) throws E {
		if (iterator.hasNext()) {
			S source = iterator.next();
			try {
				accept(source);
			} finally {
				acceptAll(iterator);
			}
		}
	}

	default <T> Consumer<T, E> map(@NonNull Function<? super T, ? extends S, ? extends E> mapper) {
		return new MappedConsumer<>(this, mapper);
	}

	default Consumer<S, E> onClose(Consumer<? super S, ? extends E> endpoint) {
		if (endpoint == null) {
			return this;
		}
		return new OnCloseConsumer<>(this, endpoint);
	}

	default Consumer<S, E> andThen(Consumer<? super S, ? extends E> after) {
		if (after == null) {
			return this;
		}
		return new AndThenConsumer<>(this, after);
	}
}
