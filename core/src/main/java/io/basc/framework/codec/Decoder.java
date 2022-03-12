package io.basc.framework.codec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;

/**
 * 解码器<br/>
 * 
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
@FunctionalInterface
public interface Decoder<E, D> {
	/**
	 * 解码
	 * 
	 * @param source
	 * @return
	 * @throws DecodeException
	 */
	D decode(E source) throws DecodeException;

	default List<D> decodeAll(Collection<? extends E> sources) throws DecodeException {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		}

		return sources.stream().map((e) -> decode(e)).collect(Collectors.toList());
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default D[] decodeAll(E... sources) throws DecodeException {
		return toDecodeConverter().convertAll(sources);
	}

	/**
	 * decode <- decode <- decode ... <br/>
	 * 
	 * @param decoder
	 * @return
	 */
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new NestedDecoder<>(decoder, this);
	}

	/**
	 * decode -> decode -> decode ...<br/>
	 * 
	 * @param decoder
	 * @return
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new NestedDecoder<>(this, decoder);
	}

	default Converter<E, D> toDecodeConverter() {
		return (o) -> decode(o);
	}
}
