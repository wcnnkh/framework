package run.soeasy.framework.codec;

/**
 * 链式解码器接口，继承自{@link Decoder}，通过组合两个解码器实现链式解码功能，
 * 支持将一个解码过程拆分为两个阶段，适用于需要多步转换的解码场景。
 * 
 * <p>该接口的核心逻辑是将源数据先通过第一个解码器转换为中间数据，
 * 再将中间数据通过第二个解码器转换为最终结果，形成"源数据→中间数据→目标数据"的链式处理流程。
 * 
 * @param <D> 源数据类型（待解码的数据类型）
 * @param <T> 中间数据类型（第一个解码器的输出，第二个解码器的输入）
 * @param <E> 目标数据类型（最终解码结果类型）
 * @author soeasy.run
 * @see Decoder
 * @see CodecException
 */
public interface ChainDecoder<D, T, E> extends Decoder<D, E> {

    /**
     * 获取用于将源数据转换为中间数据的解码器
     * 
     * @return 源到中间数据的解码器
     */
    Decoder<D, T> getFromDecoder();

    /**
     * 获取用于将中间数据转换为目标数据的解码器
     * 
     * @return 中间到目标数据的解码器
     */
    Decoder<T, E> getToDecoder();

    /**
     * 执行链式解码操作
     * 
     * <p>实现逻辑：
     * 1. 调用{@link #getFromDecoder()}将源数据解码为中间数据
     * 2. 调用{@link #getToDecoder()}将中间数据解码为目标数据
     * 
     * @param source 源数据
     * @return 最终解码结果
     * @throws CodecException 当解码过程中发生错误时抛出
     */
    @Override
    default E decode(D source) throws CodecException {
        T target = getFromDecoder().decode(source);
        return getToDecoder().decode(target);
    }
}