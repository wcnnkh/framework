package scw.codec;

/**
 * 编码器
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
public interface Encoder<D, E> {
	E encode(D source) throws EncodeException;
}