package io.basc.framework.codec;

import io.basc.framework.convert.Converter;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.core.utils.ObjectUtils;
import io.basc.framework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 编码器<br/>
 * 
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
@FunctionalInterface
public interface Encoder<D, E> {
	/**
	 * 编码
	 * 
	 * @param source
	 * @return
	 * @throws EncodeException
	 */
	E encode(D source) throws EncodeException;

	default List<E> encode(Collection<? extends D> sources) throws EncodeException {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		}

		List<E> list = new ArrayList<E>(sources.size());
		for (D source : sources) {
			list.add(encode(source));
		}
		return list;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default E[] encode(D... sources) throws DecodeException {
		return toEncodeConverter().convert(sources);
	}

	static class NestedEncoder<D, T, E> implements Encoder<D, E> {
		private final Encoder<D, T> parent;
		private final Encoder<T, E> encoder;

		public Encoder<D, T> getParent() {
			return parent;
		}

		public Encoder<T, E> getEncoder() {
			return encoder;
		}

		public NestedEncoder(Encoder<D, T> parent, Encoder<T, E> encoder) {
			this.parent = parent;
			this.encoder = encoder;
		}

		@Override
		public E encode(D source) throws EncodeException {
			return encoder.encode(parent.encode(source));
		}
	}

	/**
	 * encode <- encode <- encode ...<br/>
	 * 
	 * @param encoder
	 * @return
	 */
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new NestedEncoder<F, D, E>(encoder, this);
	}

	/**
	 * encode -> encode -> encode ... <br/>
	 * 
	 * @param encoder
	 * @return
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return new NestedEncoder<D, E, T>(this, encoder);
	}

	default Signer<D, E> toSigner() {
		return new Signer<D, E>() {

			@Override
			public E encode(D source) throws EncodeException {
				return Encoder.this.encode(source);
			}

			@Override
			public boolean verify(D source, E encode) {
				return ObjectUtils.nullSafeEquals(this.encode(source), encode);
			}
		};
	}

	default <T> Signer<D, T> toSigner(Signer<E, T> signer) {
		return new Signer<D, T>() {

			@Override
			public boolean verify(D source, T encode) {
				return signer.verify(Encoder.this.encode(source), encode);
			}

			@Override
			public T encode(D source) throws EncodeException {
				return signer.encode(Encoder.this.encode(source));
			}
		};
	}

	default Converter<D, E> toEncodeConverter() {
		return new Converter<D, E>() {

			@Override
			public E convert(D o) {
				return encode(o);
			}
		};
	}
}