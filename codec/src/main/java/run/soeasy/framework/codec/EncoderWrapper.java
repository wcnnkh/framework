package run.soeasy.framework.codec;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 编码器包装器接口，扩展自{@link Encoder}和{@link Wrapper}，
 * 用于包装其他编码器实例，支持通过装饰器模式为编码器添加额外功能（如日志记录、参数校验）。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明委托：所有编码操作透明转发给被包装的编码器{@link #getSource()}</li>
 *   <li>装饰器模式：支持在不修改原编码器的情况下添加额外功能</li>
 *   <li>类型安全：保持泛型类型一致性，确保包装前后的编码行为兼容</li>
 *   <li>链式组合：支持与其他编码器组合，形成复杂的编码流程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 源数据类型（待编码类型）
 * @param <E> 编码后数据类型
 * @param <W> 被包装的编码器类型（必须实现{@link Encoder}）
 * @see Encoder
 * @see Wrapper
 */
@FunctionalInterface
public interface EncoderWrapper<D, E, W extends Encoder<D, E>> extends Encoder<D, E>, Wrapper<W> {
    /**
     * 对源数据执行编码操作（委托给被包装的编码器）。
     * <p>
     * 该方法将编码请求透明转发给{@link #getSource()}返回的编码器，
     * 实现装饰器模式的核心委托逻辑。
     * 
     * @param source 待编码的源数据，不可为null
     * @return 编码后的数据
     * @throws CodecException 被包装编码器编码失败时抛出
     */
    @Override
    default E encode(D source) throws CodecException {
        return getSource().encode(source);
    }

    /**
     * 验证编码结果是否正确（委托给被包装的编码器）。
     * <p>
     * 该方法将验证请求透明转发给{@link #getSource()}返回的编码器，
     * 实际调用{@link Encoder#test(Object, Object)}。
     * 
     * @param source  待编码的源数据
     * @param encode  预期的编码结果
     * @return true表示编码结果与预期一致
     * @throws CodecException 被包装编码器执行编码时抛出异常
     */
    @Override
    default boolean test(D source, E encode) throws CodecException {
        return getSource().test(source, encode);
    }

    /**
     * 组合前置编码器形成新的编码器（委托给被包装的编码器）。
     * <p>
     * 新编码器的编码流程为：
     * <pre>
     * F → [前置编码器] → D → [被包装编码器] → E
     * </pre>
     * 该方法实际调用{@link Encoder#fromEncoder(Encoder)}。
     * 
     * @param <F>     前置编码器的源类型
     * @param encoder 前置编码器，不可为null
     * @return 组合后的新编码器
     */
    @Override
    default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
        return getSource().fromEncoder(encoder);
    }

    /**
     * 组合后置编码器形成新的编码器（委托给被包装的编码器）。
     * <p>
     * 新编码器的编码流程为：
     * <pre>
     * D → [被包装编码器] → E → [后置编码器] → T
     * </pre>
     * 该方法实际调用{@link Encoder#toEncoder(Encoder)}。
     * 
     * @param <T>     后置编码器的目标类型
     * @param encoder 后置编码器，不可为null
     * @return 组合后的新编码器
     */
    @Override
    default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
        return getSource().toEncoder(encoder);
    }
}