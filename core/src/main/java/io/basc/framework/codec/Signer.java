package io.basc.framework.codec;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Validator;

/**
 * 签名
 * 
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
public interface Signer<D, E> extends Encoder<D, E>, Validator<D, E> {
	/**
	 * 生成签名
	 */
	@Override
	E encode(D source) throws EncodeException;

	/**
	 * 校验签名
	 */
	@Override
	default boolean verify(D source, E encode) throws CodecException {
		return ObjectUtils.equals(this.encode(source), encode);
	}

	default <F> Signer<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new NestedEncodeSigner<>(encoder, this);
	}

	@Override
	default Signer<D, E> toSigner() {
		return this;
	}

	default <T> Signer<D, T> to(Codec<E, T> codec) {
		return new CodedSigner<>(codec, this);
	}
}
