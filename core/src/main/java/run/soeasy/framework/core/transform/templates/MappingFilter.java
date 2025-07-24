package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 映射过滤器函数式接口，用于在映射过程中对上下文和映射器进行条件过滤或增强处理。
 * <p>
 * 该接口作为映射流程中的过滤环节，允许在执行实际映射操作前对上下文状态进行检查、
 * 对映射器行为进行修改，或根据特定条件决定是否继续执行映射。适用于需要在映射过程中
 * 添加条件判断、权限控制、数据校验等横切逻辑的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>条件过滤：基于上下文状态决定是否执行映射操作</li>
 *   <li>行为增强：可修改映射器的行为或上下文属性</li>
 *   <li>函数式设计：支持通过lambda表达式快速实现简单过滤逻辑</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>执行顺序依赖：过滤结果可能依赖于过滤器的注册顺序</li>
 *   <li>状态修改风险：修改上下文状态可能影响后续映射操作</li>
 *   <li>性能影响：链式过滤可能带来额外的调用开销</li>
 *   <li>异常处理：未定义异常处理机制，异常可能中断映射流程</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，必须实现{@link TypedValueAccessor}
 * @param <T> 映射类型，必须实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see Mapper
 * @see MappingContext
 * @see TypedValueAccessor
 */
@FunctionalInterface
public interface MappingFilter<K, V extends TypedValueAccessor, T extends Mapping<K, V>> {

    /**
     * 执行映射过滤操作
     * <p>
     * 该方法作为过滤逻辑的核心实现，可：
     * <ol>
     *   <li>检查源上下文和目标上下文的状态</li>
     *   <li>修改上下文属性或映射器行为</li>
     *   <li>根据条件决定是否继续执行映射</li>
     * </ol>
     * 返回true表示允许继续执行映射，返回false表示跳过当前映射。
     * 
     * @param sourceContext 源映射上下文，不可为null
     * @param targetContext 目标映射上下文，不可为null
     * @param mapper 待执行的映射器，不可为null
     * @return true表示继续映射，false表示跳过映射
     * @throws NullPointerException 若任一参数为null
     */
    boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext, 
                      @NonNull MappingContext<K, V, T> targetContext,
                      @NonNull Mapper<K, V, T> mapper);
}