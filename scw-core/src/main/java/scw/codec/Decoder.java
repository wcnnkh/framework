package scw.codec;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.convert.Converter;
import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;

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
		if (sources == null) {
			return null;
		}

		int index = 0;
		Object array = null;
		for (int i = 0; i < sources.length; i++) {
			D target = decode(sources[i]);
			if (target != null) {
				index = i;
				array = Array.newInstance(target.getClass(), sources.length);
			}

			if (array != null) {
				Array.set(array, i, target);
			}
		}
		
		if(array == null) {
			//所有内容都为空，无法解析数组类型
		}else {
			for (int i = 0; i < index; i++) {
				Array.set(array, i, null);
			}
		}
		return (D[]) array;
	}

	static class NestedDecoder<D, T, E> implements Decoder<D, E> {
		private final Decoder<D, T> parent;
		private final Decoder<T, E> decoder;

		public NestedDecoder(Decoder<D, T> parent, Decoder<T, E> decoder) {
			this.parent = parent;
			this.decoder = decoder;
		}

		public Decoder<D, T> getParent() {
			return parent;
		}

		public Decoder<T, E> getDecoder() {
			return decoder;
		}

		@Override
		public E decode(D source) throws DecodeException {
			return decoder.decode(parent.decode(source));
		}
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
