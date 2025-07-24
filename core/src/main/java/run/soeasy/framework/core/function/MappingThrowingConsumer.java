package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 映射型可抛出异常的消费者实现，支持将输入类型进行转换后执行一系列消费操作，
 * 并在执行过程中进行异常类型转换和资源清理。该实现允许按顺序执行多个消费操作，
 * 并在操作前后分别进行异常处理和资源清理。
 *
 * <p>执行流程：
 * <ol>
 *   <li>使用mapper将输入类型T转换为中间类型S</li>
 *   <li>执行compose消费操作</li>
 *   <li>执行andThen消费操作</li>
 *   <li>若执行过程中抛出异常，通过throwingMapper进行异常类型转换</li>
 *   <li>无论执行是否成功，最终执行endpoint进行资源清理</li>
 * </ol>
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型转换：支持将输入类型T映射为中间类型S</li>
 *   <li>操作组合：支持按顺序执行多个消费操作</li>
 *   <li>异常转换：可将原始异常类型E转换为目标异常类型R</li>
 *   <li>资源管理：确保无论执行结果如何，资源清理操作都会被执行</li>
 *   <li>不可变设计：所有依赖项在构造时注入，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对输入进行类型转换后再消费的场景</li>
 *   <li>需要组合多个消费操作并统一异常处理的场景</li>
 *   <li>执行后必须释放资源的操作（如文件、网络连接等）</li>
 *   <li>需要将底层异常转换为业务异常的场景</li>
 * </ul>
 *
 * @param <S> 中间类型，即输入类型T经过映射后得到的类型
 * @param <E> 原始异常类型，即compose可能抛出的异常类型
 * @param <T> 输入类型，即accept方法接收的参数类型
 * @param <R> 目标异常类型，即最终抛出的异常类型
 * @see ThrowingConsumer
 */
@RequiredArgsConstructor
@Getter
class MappingThrowingConsumer<S, E extends Throwable, T, R extends Throwable> implements ThrowingConsumer<T, R> {
    
    /**
     * 类型映射函数，将输入类型T转换为中间类型S。
     */
    @NonNull
    private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
    
    /**
     * 前置消费操作，在主操作执行前执行。
     */
    @NonNull
    private final ThrowingConsumer<? super S, ? extends E> compose;
    
    /**
     * 主消费操作，在前置操作执行后执行。
     */
    @NonNull
    private final ThrowingConsumer<? super S, ? extends R> andThen;
    
    /**
     * 异常转换函数，将compose抛出的异常类型E转换为目标异常类型R。
     */
    @NonNull
    private final Function<? super E, ? extends R> throwingMapper;
    
    /**
     * 资源清理操作，无论执行成功或失败都会在finally块中执行。
     */
    @NonNull
    private final ThrowingConsumer<? super S, ? extends R> endpoint;

    /**
     * 执行消费操作流程：
     * <ol>
     *   <li>使用mapper将输入类型T转换为中间类型S</li>
     *   <li>执行compose消费操作</li>
     *   <li>执行andThen消费操作</li>
     *   <li>若执行过程中抛出异常，通过throwingMapper进行异常类型转换</li>
     *   <li>无论执行是否成功，最终执行endpoint进行资源清理</li>
     * </ol>
     *
     * @param target 输入参数
     * @throws R 执行过程中抛出的异常，经过throwingMapper转换后的类型
     */
    @SuppressWarnings("unchecked")
    @Override
    public void accept(T target) throws R {
        S source = mapper.apply(target);
        try {
            compose.accept(source);
            andThen.accept(source);
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        } finally {
            endpoint.accept(source);
        }
    }
}