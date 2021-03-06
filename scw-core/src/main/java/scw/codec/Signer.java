package scw.codec;

public interface Signer<D, E> extends Encoder<D, E>, Validator<D, E> {
	/**
	 * 签名
	 */
	E encode(D source) throws EncodeException;
}
