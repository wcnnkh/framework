package io.basc.framework.codec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Validator;
import io.basc.framework.util.stream.Processor;

/**
 * 编码器<br/>
 * 
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
@FunctionalInterface
public interface Encoder<D, E> extends Validator<D, E> {
	/**
	 * 编码
	 * 
	 * @param source
	 * @return
	 * @throws EncodeException
	 */
	E encode(D source) throws EncodeException;

	/**
	 * 校验
	 */
	@Override
	default boolean verify(D source, E encode) throws EncodeException {
		return ObjectUtils.equals(this.encode(source), encode);
	}

	default List<E> encodeAll(Collection<? extends D> sources) throws EncodeException {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		}

		return sources.stream().map((e) -> encode(e)).collect(Collectors.toList());
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default E[] encodeAll(D... sources) throws DecodeException {
		return toEncodeProcessor().processAll(sources);
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

	default Processor<D, E, EncodeException> toEncodeProcessor() {
		return (o) -> encode(o);
	}
}