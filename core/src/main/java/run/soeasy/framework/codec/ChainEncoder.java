package run.soeasy.framework.codec;

/**
 * 链式编码器接口，继承自{@link Encoder}，通过组合两个编码器实现链式编码功能，
 * 支持将一个编码过程拆分为两个阶段，适用于需要多步转换的编码场景。
 * 
 * <p>该接口的核心逻辑是将源数据先通过第一个编码器转换为中间数据，
 * 再将中间数据通过第二个编码器转换为最终结果，形成"源数据→中间数据→目标数据"的链式处理流程。
 * 
 * @param <D> 源数据类型
 * @param <T> 中间数据类型（第一个编码器的输出，第二个编码器的输入）
 * @param <E> 目标数据类型（最终编码结果类型）
 * @author soeasy.run
 * @see Encoder
 * @see CodecException
 */
public interface ChainEncoder<D, T, E> extends Encoder<D, E> {

    /**
     * 获取用于将源数据转换为中间数据的编码器
     * 
     * @return 源到中间数据的编码器
     */
    Encoder<D, T> getFromEncoder();

    /**
     * 获取用于将中间数据转换为目标数据的编码器
     * 
     * @return 中间到目标数据的编码器
     */
    Encoder<T, E> getToEncoder();

    /**
     * 执行链式编码操作
     * 
     * <p>实现逻辑：
     * 1. 调用{@link #getFromEncoder()}将源数据编码为中间数据
     * 2. 调用{@link #getToEncoder()}将中间数据编码为目标数据
     * 
     * @param source 源数据
     * @return 最终编码结果
     * @throws CodecException 当编码过程中发生错误时抛出
     */
    @Override
    default E encode(D source) throws CodecException {
        return getToEncoder().encode(getFromEncoder().encode(source));
    }
}