package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 映射型可抛出异常的函数实现，支持按顺序组合多个函数操作，
 * 并在执行过程中进行异常类型转换和资源清理。该实现允许将输入类型S经过
 * 中间类型T转换为最终类型V，同时统一处理各步骤的异常并确保资源释放。
 *
 * <p>执行流程：
 * <ol>
 *   <li>执行compose函数将S转换为T</li>
 *   <li>执行andThen函数将T转换为V</li>
 *   <li>若任意步骤抛出异常，通过throwingMapper转换异常类型</li>
 *   <li>（注：当前实现中finally块被注释，资源清理可能未生效）</li>
 * </ol>
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数组合：支持将compose和andThen函数按顺序链式调用</li>
 *   <li>类型转换：支持输入类型S→中间类型T→输出类型V的级联转换</li>
 *   <li>异常封装：通过throwingMapper统一转换异常类型为R</li>
 *   <li>资源管理：预留endpoint用于资源清理（当前实现未启用）</li>
 *   <li>不可变设计：所有依赖项在构造时注入，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要多步类型转换并统一异常处理的复杂映射场景</li>
 *   <li>微服务间数据格式转换时的异常标准化处理</li>
 *   <li>需要将底层技术异常转换为业务异常的场景</li>
 *   <li>组合多个函数操作并确保资源清理的场景（需修复finally块）</li>
 * </ul>
 *
 * @param <S> 输入类型，即apply方法的参数类型
 * @param <T> 中间转换类型，compose函数的输出类型和andThen函数的输入类型
 * @param <E> 原始异常类型，即compose函数可能抛出的异常类型
 * @param <V> 输出类型，即apply方法的返回类型
 * @param <R> 目标异常类型，即最终抛出的异常类型
 * @see ThrowingFunction
 */
@RequiredArgsConstructor
@Getter
class MappingThrowingFunction<S, T, E extends Throwable, V, R extends Throwable> implements ThrowingFunction<S, V, R> {
    
    /**
     * 前置转换函数，将输入类型S转换为中间类型T。
     * 该函数的输出作为andThen函数的输入。
     */
    @NonNull
    private final ThrowingFunction<? super S, ? extends T, ? extends E> compose;
    
    /**
     * 后置转换函数，将中间类型T转换为最终类型V。
     * 该函数的输入为compose函数的输出。
     */
    @NonNull
    private final ThrowingFunction<? super T, ? extends V, ? extends R> andThen;
    
    /**
     * 异常转换函数，将原始异常类型E转换为目标异常类型R。
     * 用于统一各步骤的异常类型。
     */
    @NonNull
    private final Function<? super E, ? extends R> throwingMapper;
    
    /**
     * 资源清理消费者，用于处理中间类型T的资源释放。
     * （注：当前apply方法的finally块被注释，该消费者未实际调用）
     */
    @NonNull
    private final ThrowingConsumer<? super T, ? extends E> endpoint;

    /**
     * 按顺序执行compose和andThen函数，并处理异常转换。
     * 执行流程：
     * <ol>
     *   <li>调用compose函数将S转换为T，捕获异常并转换为R</li>
     *   <li>调用andThen函数将T转换为V，捕获异常并转换为R</li>
     *   <li>（注：finally块被注释，endpoint未执行）</li>
     * </ol>
     *
     * @param source 输入参数
     * @return 转换后的最终类型V
     * @throws R 转换过程中抛出的异常（经throwingMapper转换后）
     */
    @SuppressWarnings("unchecked")
    @Override
    public V apply(S source) throws R {
        T value;
        try {
            value = compose.apply(source);
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        }

        try {
            return andThen.apply(value);
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        } finally {
            // 注意：当前实现中endpoint未被调用，资源清理逻辑需要手动启用
            // endpoint.accept(value);
        }
    }
}