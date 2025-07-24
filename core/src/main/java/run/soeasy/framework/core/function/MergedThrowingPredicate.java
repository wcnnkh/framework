package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 合并型可抛出异常的谓词实现，支持将输入值通过映射函数转换后，
 * 应用目标谓词进行判断，并在判断完成后执行资源清理操作，同时支持异常类型转换。
 * 该实现允许将多个操作合并为一个逻辑单元，简化复杂条件判断流程。
 *
 * <p>核心特性：
 * <ul>
 *   <li>值映射：通过{@link ThrowingFunction}将输入值转换为目标类型</li>
 *   <li>条件判断：应用{@link ThrowingPredicate}对映射后的值进行判断</li>
 *   <li>资源管理：支持在判断完成后执行清理操作（如关闭文件、释放连接等）</li>
 *   <li>异常转换：通过{@link Function}将源异常类型转换为目标异常类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对输入值进行预处理后再进行条件判断的场景</li>
 *   <li>条件判断后需要自动释放相关资源的场景</li>
 *   <li>统一不同模块抛出的异常类型</li>
 *   <li>组合多个谓词形成复杂判断逻辑的场景</li>
 * </ul>
 *
 * @param <S> 映射后的值类型，即目标谓词的输入类型
 * @param <E> 目标谓词可能抛出的异常类型
 * @param <T> 原始输入值类型
 * @param <R> 最终抛出的异常类型，源异常会被转换为此类型
 * @see ThrowingPredicate
 * @see ThrowingFunction
 */
@RequiredArgsConstructor
@Getter
class MergedThrowingPredicate<S, E extends Throwable, T, R extends Throwable> implements ThrowingPredicate<T, R> {
    
    /**
     * 映射函数，将原始输入值转换为目标谓词可处理的类型。
     */
    @NonNull
    private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
    
    /**
     * 目标谓词，用于对映射后的值进行条件判断。
     */
    @NonNull
    private final ThrowingPredicate<? super S, ? extends E> predicate;
    
    /**
     * 异常转换函数，将目标谓词抛出的异常转换为最终异常类型。
     */
    @NonNull
    private final Function<? super E, ? extends R> throwingMapper;
    
    /**
     * 资源清理消费者，在判断完成后执行清理操作。
     */
    @NonNull
    private final ThrowingConsumer<? super S, ? extends R> endpoint;

    /**
     * 对输入值进行映射、判断和清理的完整流程。
     * 该方法会先应用映射函数转换输入值，再使用目标谓词进行判断，
     * 最后执行清理操作，并在整个过程中处理可能抛出的异常。
     *
     * @param target 原始输入值
     * @return 条件判断结果
     * @throws R 可能抛出的最终异常类型
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean test(T target) throws R {
        S source = mapper.apply(target);
        try {
            return predicate.test(source);
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        } finally {
            endpoint.accept(source);
        }
    }
}