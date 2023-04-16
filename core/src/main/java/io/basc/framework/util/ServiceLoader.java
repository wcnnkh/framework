package io.basc.framework.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ServiceLoader<S> extends Elements<S> {
	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> empty() {
		return (ServiceLoader<T>) EmptyServiceLoader.EMPTY;
	}

	public static <T> ServiceLoader<T> of(Iterable<T> elements) {
		if (elements == null) {
			return empty();
		}

		if (elements instanceof ServiceLoader) {
			return (ServiceLoader<T>) elements;
		}
		return new SharedServiceLoader<>(Elements.of(elements));
	}

	@Override
	default <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertibleServiceLoader<>(this, converter);
	}

	@Override
	default ServiceLoader<S> filter(Predicate<? super S> predicate) {
		return convert((stream) -> stream.filter(predicate));
	}

	@Override
	default <U> ServiceLoader<U> flatMap(Function<? super S, ? extends Streamable<U>> mapper) {
		// TODO 如果展开后又是一个serviceloader,那么realod应该全部reload吗
		return convert((stream) -> {
			return stream.flatMap((e) -> {
				Streamable<U> streamy = mapper.apply(e);
				return streamy == null ? Stream.empty() : streamy.stream();
			});
		});
	}

	@Override
	default <U> ServiceLoader<U> map(Function<? super S, ? extends U> mapper) {
		return convert((stream) -> stream.map(mapper));
	}

	void reload();

	/**
	 * 因为存在reload，所以默认的实现都是放在内存中的，那么只用实现{@link #iterator()}就可以了
	 */
	@Override
	default Stream<S> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
}
