package scw.codec;

/**
 * 编解码器
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D>{
}
