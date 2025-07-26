package run.soeasy.framework.codec;

/**
 * 编解码器包装器接口，扩展自{@link Codec}、{@link EncoderWrapper}和{@link DecoderWrapper}，
 * 用于包装其他编解码器实例，支持通过装饰器模式为编解码器添加额外功能
 * （如日志记录、性能监控、参数校验）。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明委托：所有编解码操作透明转发给被包装的编解码器{@link #getSource()}</li>
 *   <li>装饰器模式：支持在不修改原编解码器的情况下添加额外功能</li>
 *   <li>双向增强：同时增强编码和解码能力</li>
 *   <li>链式组合：支持与其他编解码器组合，形成复杂的编解码流程</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志增强：记录编解码过程和结果</li>
 *   <li>性能监控：统计编解码操作的耗时和性能指标</li>
 *   <li>参数校验：在编解码前对输入参数进行有效性检查</li>
 *   <li>异常转换：将底层编解码器的异常转换为统一格式</li>
 *   <li>安全增强：在编解码前后添加安全检查或数据脱敏</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 源数据类型（解码后类型，编码前类型）
 * @param <E> 目标数据类型（编码后类型，解码前类型）
 * @param <W> 被包装的编解码器类型（必须实现{@link Codec}）
 * @see Codec
 * @see EncoderWrapper
 * @see DecoderWrapper
 */
@FunctionalInterface
public interface CodecWrapper<D, E, W extends Codec<D, E>>
        extends Codec<D, E>, EncoderWrapper<D, E, W>, DecoderWrapper<E, D, W> {
    /**
     * 组合后置编解码器形成新的编解码器（委托给被包装的编解码器）。
     * <p>
     * 新编解码器的编解码流程为：
     * <pre>
     * 编码：D → [被包装编解码器] → E → [后置编解码器] → T
     * 解码：T → [后置编解码器] → E → [被包装编解码器] → D
     * </pre>
     * 该方法实际调用{@link W#to(Codec)}。
     * 
     * @param <T>   后置编解码器的目标类型
     * @param codec 后置编解码器，不可为null
     * @return 组合后的新编解码器
     */
    @Override
    default <T> Codec<D, T> to(Codec<E, T> codec) {
        return getSource().to(codec);
    }

    /**
     * 组合前置编解码器形成新的编解码器（委托给被包装的编解码器）。
     * <p>
     * 新编解码器的编解码流程为：
     * <pre>
     * 编码：F → [前置编解码器] → D → [被包装编解码器] → E
     * 解码：E → [被包装编解码器] → D → [前置编解码器] → F
     * </pre>
     * 该方法实际调用{@link W#from(Codec)}。
     * 
     * @param <F>   前置编解码器的源类型
     * @param codec 前置编解码器，不可为null
     * @return 组合后的新编解码器
     */
    @Override
    default <F> Codec<F, E> from(Codec<F, D> codec) {
        return getSource().from(codec);
    }

    /**
     * 获取编解码方向相反的新编解码器（委托给被包装的编解码器）。
     * <p>
     * 原编解码器的编解码操作将变为新编解码器的反向操作：
     * <pre>
     * 原编解码器：encode(D) → E，decode(E) → D
     * 反转后：    encode(E) → D，decode(D) → E
     * </pre>
     * 
     * @return 反向编解码器实例
     */
    @Override
    default Codec<E, D> reverse() {
        return getSource().reverse();
    }
}