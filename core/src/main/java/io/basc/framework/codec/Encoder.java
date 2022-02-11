package io.basc.framework.codec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;

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

	/**
	 * encode <- encode <- encode ...<br/>
	 * 
	 * @param encoder
	 * @return
	 */
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new NestedEncoder<>(encoder, this);
	}

	/**
	 * encode -> encode -> encode ... <br/>
	 * 
	 * @param encoder
	 * @return
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return new NestedEncoder<>(this, encoder);
	}

	default Signer<D, E> toSigner() {
		return (source) -> Encoder.this.encode(source);
	}

	default <T> Signer<D, T> toSigner(Signer<E, T> signer) {
		return signer.fromEncoder(this);
	}

	default Converter<D, E> toEncodeConverter() {
		return (o) -> encode(o);
	}
}