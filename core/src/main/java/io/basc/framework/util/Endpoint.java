package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个终点的定义
 * 
 * @see Consumer
 * @author wcnnkh
 *
 * @param <S> 回调的数据类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface Endpoint<S, E extends Throwable> {
	@FunctionalInterface
	public interface EndpointWrapper<S, E extends Throwable, W extends Endpoint<S, E>>
			extends Endpoint<S, E>, Wrapper<W> {
		@Override
		default void accept(S source) throws E {
			getSource().accept(source);
		}

		@Override
		default <T> Endpoint<T, E> map(@NonNull Pipeline<? super T, ? extends S, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedEndpoint<S, T, E extends Throwable, W extends Endpoint<T, E>>
			implements Endpoint<S, E>, Wrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super S, ? extends T, ? extends E> mapper;

		@Override
		public void accept(S source) throws E {
			T target = mapper.apply(source);
			this.source.accept(target);
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
	public static class RejectEndpoint<A, B extends Throwable> implements Endpoint<A, B> {

		@Override
		public void accept(A source) throws B {
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Endpoint<T, B> map(@NonNull Pipeline<? super T, ? extends A, ? extends B> mapper) {
			return (Endpoint<T, B>) REJECT_ENDPOINT;
		}
	}

	public static final RejectEndpoint<?, ?> REJECT_ENDPOINT = new RejectEndpoint<>();

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Endpoint<T, E> reject() {
		return (Endpoint<T, E>) REJECT_ENDPOINT;
	}

	void accept(S source) throws E;

	public static <S, E extends Throwable> void acceptAll(@NonNull Streamable<? extends S> streamable,
			@NonNull Endpoint<? super S, ? extends E> endpoint) throws E {
		Stream<? extends S> stream = streamable.stream();
		try {
			endpoint.acceptAll(stream.iterator());
		} finally {
			stream.close();
		}
	}

	public static <S, E extends Throwable> void acceptAll(@NonNull Iterator<? extends S> iterator,
			@NonNull Endpoint<? super S, ? extends E> endpoint) throws E {
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

	default <T> Endpoint<T, E> map(@NonNull Pipeline<? super T, ? extends S, ? extends E> mapper) {
		return new MappedEndpoint<>(this, mapper);
	}

	default Endpoint<S, E> onClose(Endpoint<? super S, ? extends E> endpoint) {
		return null;
	}
}
