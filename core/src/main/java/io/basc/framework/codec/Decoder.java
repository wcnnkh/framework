package io.basc.framework.codec;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

	default List<D> decode(Collection<? extends E> sources) throws DecodeException {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		}

		List<D> list = new ArrayList<D>(sources.size());
		for (E source : sources) {
			list.add(decode(source));
		}
		return list;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default D[] decode(E... sources) throws DecodeException {
		return toDecodeConverter().convert(sources);
	}

	/**
	 * decode <- decode <- decode ... <br/>
	 * 
	 * @param decoder
	 * @return
	 */
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new NestedDecoder<F, E, D>(decoder, this);
	}

	/**
	 * decode -> decode -> decode ...<br/>
	 * 
	 * @param decoder
	 * @return
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new NestedDecoder<E, D, T>(this, decoder);
	}

	default Converter<E, D> toDecodeConverter() {
		return new Converter<E, D>() {

			@Override
			public D convert(E o) {
				return decode(o);
			}
		};
	}
}
