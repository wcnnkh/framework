package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的 Runnable 接口，扩展了 Java 标准库的 Runnable 接口，允许在 run 方法中抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的任务场景，并提供了丰富的链式操作方法用于异常处理和任务组合。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口：可作为 lambda 表达式或方法引用的目标类型</li>
 *   <li>异常处理：允许在 run() 方法中抛出指定类型的异常 E</li>
 *   <li>任务组合：提供 compose/andThen 方法实现任务的顺序执行</li>
 *   <li>异常转换：通过 throwing 方法实现异常类型的统一转换</li>
 *   <li>回调注册：通过 onClose 方法注册资源关闭时的回调任务</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要抛出特定异常的资源清理操作</li>
 *   <li>异步任务中需要处理异常的场景</li>
 *   <li>组合多个可能抛出异常的任务序列</li>
 *   <li>统一处理不同异常类型的任务执行</li>
 * </ul>
 *
 * @param <E> 可能抛出的异常类型，必须是 Throwable 的子类
 * @see java.lang.Runnable
 * @see ThrowingSupplier
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

    /**
     * 获取忽略异常的 ThrowingRunnable 实例。
     * 该实例的 run() 方法无实际操作，且不会抛出异常，适用于需要占位的场景。
     *
     * @param <E> 异常类型
     * @return 忽略异常的 ThrowingRunnable 实例
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> ThrowingRunnable<E> ignore() {
        return (IgnoreThrowingRunnable<E>) IgnoreThrowingRunnable.INSTANCE;
    }

    /**
     * 组合任务，先执行 before 任务，再执行当前任务。
     * 返回的新任务会按顺序执行 before 和当前任务，若任一任务抛出异常则中断。
     *
     * @param before 先执行的任务，不可为 null
     * @return 组合后的任务实例
     */
    default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
        return new ChainThrowingRunnable<>(before, this, Function.identity(), ignore());
    }

    /**
     * 组合任务，先执行当前任务，再执行 after 任务。
     * 返回的新任务会按顺序执行当前和 after 任务，若当前任务抛出异常则不执行 after 任务。
     *
     * @param after 后执行的任务，不可为 null
     * @return 组合后的任务实例
     */
    default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
        return new ChainThrowingRunnable<>(this, after, Function.identity(), ignore());
    }

    /**
     * 转换异常类型，返回新的 ThrowingRunnable 实例。
     * 该方法允许将原始异常 E 转换为新的异常类型 R，实现异常类型的统一处理。
     *
     * @param <R>               新的异常类型，必须是 Throwable 的子类
     * @param throwingMapper    异常转换函数，不可为 null
     * @return 异常类型转换后的任务实例
     */
    default <R extends Throwable> ThrowingRunnable<R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return new ChainThrowingRunnable<>(this, ignore(), throwingMapper, ignore());
    }

    /**
     * 注册资源关闭时的回调任务。
     * 当当前任务执行完毕或抛出异常时，会调用指定的 endpoint 任务。
     *
     * @param endpoint 关闭时执行的回调任务，不可为 null
     * @return 注册回调后的任务实例
     */
    default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
        return new ChainThrowingRunnable<>(this, ignore(), Function.identity(), endpoint);
    }

    /**
     * 执行任务，可能抛出异常 E。
     * 该方法是函数式接口的抽象方法，必须由实现类提供具体实现。
     *
     * @throws E 可能抛出的指定类型异常
     */
    void run() throws E;
}