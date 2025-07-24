package run.soeasy.framework.codec;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 解码器包装器接口，扩展自{@link Decoder}和{@link Wrapper}，
 * 用于包装其他解码器实例，支持通过装饰器模式为解码器添加额外功能（如日志记录、异常处理）。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明委托：所有解码操作透明转发给被包装的解码器{@link #getSource()}</li>
 *   <li>装饰器模式：支持在不修改原解码器的情况下添加额外功能</li>
 *   <li>类型安全：保持泛型类型一致性，确保包装前后的解码行为兼容</li>
 *   <li>链式组合：支持与其他解码器组合，形成复杂的解码流程</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志增强：包装解码器以记录解码过程和结果</li>
 *   <li>性能监控：添加解码操作的耗时统计和性能指标收集</li>
 *   <li>异常转换：将底层解码器的异常转换为统一格式</li>
 *   <li>缓存优化：为解码器添加结果缓存功能</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 源数据类型（待解码类型）
 * @param <D> 解码后数据类型
 * @param <W> 被包装的解码器类型（必须实现{@link Decoder<E, D>}）
 * @see Decoder
 * @see Wrapper
 */
@FunctionalInterface
public interface DecoderWrapper<E, D, W extends Decoder<E, D>> extends Decoder<E, D>, Wrapper<W> {
    /**
     * 对源数据执行解码操作（委托给被包装的解码器）。
     * <p>
     * 该方法将解码请求透明转发给{@link #getSource()}返回的解码器，
     * 实现装饰器模式的核心委托逻辑。
     * 
     * @param source 待解码的源数据，不可为null
     * @return 解码后的数据
     * @throws CodecException 被包装解码器解码失败时抛出
     */
    @Override
    default D decode(E source) throws CodecException {
        return getSource().decode(source);
    }

    /**
     * 组合前置解码器形成新的解码器（委托给被包装的解码器）。
     * <p>
     * 新解码器的解码流程为：
     * <pre>
     * F → [前置解码器] → E → [被包装解码器] → D
     * </pre>
     * 该方法实际调用{@link W#fromDecoder(Decoder)}。
     * 
     * @param <F>     前置解码器的源类型
     * @param decoder 前置解码器，不可为null
     * @return 组合后的新解码器
     */
    @Override
    default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
        return getSource().fromDecoder(decoder);
    }

    /**
     * 组合后置解码器形成新的解码器（委托给被包装的解码器）。
     * <p>
     * 新解码器的解码流程为：
     * <pre>
     * E → [被包装解码器] → D → [后置解码器] → T
     * </pre>
     * 该方法实际调用{@link W#toDecoder(Decoder)}。
     * 
     * @param <T>     后置解码器的目标类型
     * @param decoder 后置解码器，不可为null
     * @return 组合后的新解码器
     */
    @Override
    default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
        return getSource().toDecoder(decoder);
    }
}