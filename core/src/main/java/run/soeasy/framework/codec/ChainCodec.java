package run.soeasy.framework.codec;

/**
 * 链式编解码器接口，继承自{@link Codec}、{@link ChainEncoder}和{@link ChainDecoder}，
 * 整合了链式编码与链式解码功能，支持通过两个中间编解码器实现"源数据→中间数据→目标数据"的双向转换，
 * 是框架中处理复杂数据转换的核心接口。
 * 
 * <p>该接口通过组合链式编码和链式解码能力，实现了完整的双向数据转换流程：
 * - 编码：源数据 → 中间数据 → 目标数据
 * - 解码：目标数据 → 中间数据 → 源数据
 * 适用于需要多步转换的复杂编解码场景，如多层加密解密、数据格式嵌套转换等。
 * 
 * @param <D> 源数据类型（解码的目标类型，编码的源类型）
 * @param <T> 中间数据类型（编码和解码过程中的过渡类型）
 * @param <E> 目标数据类型（编码的目标类型，解码的源类型）
 * @author soeasy.run
 * @see Codec
 * @see ChainEncoder
 * @see ChainDecoder
 */
public interface ChainCodec<D, T, E> extends Codec<D, E>, ChainEncoder<D, T, E>, ChainDecoder<E, T, D> {
}