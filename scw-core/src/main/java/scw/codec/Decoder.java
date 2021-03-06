package scw.codec;

/**
 * 解码器
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
public interface Decoder<E, D> {
	D decode(E source) throws DecodeException;
}
