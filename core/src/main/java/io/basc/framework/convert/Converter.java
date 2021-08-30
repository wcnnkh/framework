package io.basc.framework.convert;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.Decoder;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;

import java.lang.reflect.Array;
import java.util.Collection;

@Ignore
@FunctionalInterface
public interface Converter<S, T> {
	T convert(S o);

	default <E> Converter<S, E> to(Converter<T, E> converter) {
		return new Converter<S, E>() {

			@Override
			public E convert(S o) {
				return converter.convert(Converter.this.convert(o));
			}
		};
	}

	default <F> Converter<F, T> from(Converter<F, S> converter) {
		return new Converter<F, T>() {

			@Override
			public T convert(F o) {
				return Converter.this.convert(converter.convert(o));
			}
		};
	}

	default Encoder<S, T> toEncoder() {
		return new Encoder<S, T>() {

			@Override
			public T encode(S source) throws EncodeException {
				return convert(source);
			}
		};
	}

	default Decoder<S, T> toDecoder() {
		return new Decoder<S, T>() {

			@Override
			public T decode(S source) throws DecodeException {
				return convert(source);
			}
		};
	}

	default <TL extends Collection<T>> TL convert(Collection<? extends S> sourceList, TL targetList) {
		if (sourceList == null) {
			return targetList;
		}

		for (S source : sourceList) {
			T target = convert(source);
			targetList.add(target);
		}
		return targetList;
	}

	@SuppressWarnings("unchecked")
	default <TL extends Collection<T>> TL convert(Collection<? extends S> sources) {
		if (sources == null) {
			return null;
		}

		if (sources.isEmpty()) {
			return CollectionFactory.empty(sources.getClass());
		}

		TL collection = (TL) CollectionFactory.createCollection(sources.getClass(),
				CollectionFactory.getEnumSetElementType(sources), sources.size());
		for (S source : sources) {
			T target = convert(source);
			collection.add(target);
		}
		return collection;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default T[] convert(S... sources) {
		if (sources == null) {
			return null;
		}

		Object array = null;
		for (int i = 0; i < sources.length; i++) {
			T target = convert(sources[i]);
			if (target != null) {
				array = Array.newInstance(target.getClass(), sources.length);
			}

			if (array != null) {
				Array.set(array, i, target);
			}
		}
		return (T[]) array;
	}

	default void convert(S[] sources, T[] targets) {
		convert(sources, 0, targets, 0);
	}

	default void convert(S[] sources, int sourceIndex, T[] targets, int targetIndex) {
		Assert.requiredArgument(sources != null, "sources");
		Assert.requiredArgument(targets != null, "targets");

		for (int i = sourceIndex, insertIndex = targetIndex; sourceIndex < sources.length; i++, insertIndex++) {
			S source = sources[i];
			T target = convert(source);
			targets[insertIndex] = target;
		}
	}
}
