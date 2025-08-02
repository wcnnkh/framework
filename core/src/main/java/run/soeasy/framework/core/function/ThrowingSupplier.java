package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的供应者接口，扩展了Java标准库的Supplier接口，允许在获取结果时抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的供应操作场景， 并提供了丰富的链式操作方法用于异常处理和资源管理。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>函数式接口：可作为lambda表达式或方法引用的目标类型</li>
 * <li>异常处理：允许在get()方法中抛出指定类型的异常E</li>
 * <li>链式操作：提供map、throwing等方法实现函数式编程风格的链式调用</li>
 * <li>资源管理：通过closeable、onClose等方法支持资源关闭回调</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>需要抛出特定异常的资源获取操作</li>
 * <li>结合try-with-resources模式的资源供应</li>
 * <li>需要异常转换的函数式编程场景</li>
 * <li>需要统一处理异常的供应操作链</li>
 * </ul>
 *
 * @param <T> 供应结果的类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see ThrowingFunction
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    /**
     * 获取供应的结果，可能抛出指定类型的异常
     * 
     * @return 供应的结果对象，类型为T
     * @throws E 可能抛出的异常，类型为E
     */
    T get() throws E;

    /**
     * 创建一个支持资源关闭的管道（Pipeline），用于管理资源生命周期
     * 
     * @return 包含当前供应者的Pipeline实例，可进行后续的资源关闭操作
     */
    default Pipeline<T, E> closeable() {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                false, ThrowingRunnable.ignore());
    }

    /**
     * 注册一个资源关闭时的消费回调，返回支持资源池操作的Pool实例
     * 
     * @param consumer 资源关闭时执行的消费操作，非空
     * @return 包含当前供应者和关闭回调的Pool实例
     */
    default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
        return new ChainPool<>(this, Function.identity(), consumer);
    }

    /**
     * 注册一个资源关闭时的无参回调，返回支持管道操作的Pipeline实例
     * 
     * @param closeable 资源关闭时执行的无参操作，非空
     * @return 包含当前供应者和关闭回调的Pipeline实例
     */
    default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
        return new run.soeasy.framework.core.function.ChainPipeline<>(this, ThrowingFunction.identity(),
                ThrowingConsumer.ignore(), Function.identity(), false, closeable);
    }

    /**
     * 对供应结果进行映射转换，返回新的ThrowingSupplier实例
     * 
     * @param <R> 映射后的结果类型
     * @param mapper 用于转换结果的函数，非空，可能抛出异常E
     * @return 新的ThrowingSupplier，其结果为映射后的类型R
     */
    default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
        return new ChainThrowingSupplier<>(this, mapper, ThrowingConsumer.ignore(), Function.identity(), false);
    }

    /**
     * 转换异常类型，将原有异常E转换为新的异常类型R
     * 
     * @param <R> 转换后的异常类型，必须是Throwable的子类
     * @param throwingMapper 用于转换异常的函数，非空
     * @return 新的ThrowingSupplier，其异常类型为R
     */
    default <R extends Throwable> ThrowingSupplier<T, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return new ChainThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper,
                false);
    }

    /**
     * 创建一个单例模式的ThrowingSupplier，确保结果只被计算一次
     * 
     * @return 单例模式的ThrowingSupplier实例
     */
    default ThrowingSupplier<T, E> singleton() {
        return new ChainThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
                Function.identity(), true);
    }

    /**
     * 将供应结果包装为支持异常处理的Optional容器
     * 
     * @return 包含当前供应者的ThrowingOptional实例
     */
    default ThrowingOptional<T, E> optional() {
        return new ChainThrowingOptional<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
                Function.identity(), false);
    }

    /**
     * 类型转换工具方法，将供应者的结果和异常转换为指定类型
     * 
     * @param <A> 目标结果类型
     * @param <B> 目标异常类型
     * @param requriedType 目标结果类型的Class对象
     * @param throwingType 目标异常类型的Class对象
     * @param throwingSupplier 待转换的ThrowingSupplier实例，非空
     * @return 转换后的ThrowingSupplier，结果类型为A，异常类型为B
     */
    public static <A, B extends Throwable> ThrowingSupplier<A, B> cast(Class<A> requriedType, Class<B> throwingType,
            @NonNull ThrowingSupplier<? extends A, ? extends B> throwingSupplier) {
        return throwingSupplier.map(requriedType::cast).throwing(throwingType::cast);
    }
}
    