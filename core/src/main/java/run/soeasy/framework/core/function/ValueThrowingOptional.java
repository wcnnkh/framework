package run.soeasy.framework.core.function;

import lombok.NonNull;

/**
 * 固定值的可抛出异常的Optional实现，封装一个可能为null的值，
 * 提供Optional风格的安全值访问和函数式操作，同时支持抛出指定类型的异常。
 * 该类继承自{@link ValueThrowingSupplier}，实现了{@link ThrowingOptional}接口，
 * 兼具供应者和Optional的双重特性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>空值安全：通过{@link #EMPTY}表示空值状态，避免NullPointerException</li>
 *   <li>异常支持：所有操作均支持抛出指定类型的异常{@code E}</li>
 *   <li>函数式操作：提供flatMap/map等方法实现值的转换和组合</li>
 *   <li>不可变设计：封装的值在创建后不可更改，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要安全处理可能为null的值并抛出异常的场景</li>
 *   <li>函数式编程中需要异常处理的Optional风格操作</li>
 *   <li>作为默认空值或固定值的占位符</li>
 *   <li>测试环境中模拟Optional类型的返回值</li>
 * </ul>
 *
 * @param <T> 封装的值类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see ThrowingOptional
 * @see ValueThrowingSupplier
 */
public class ValueThrowingOptional<T, E extends Throwable> extends ValueThrowingSupplier<T, E>
        implements ThrowingOptional<T, E> {
    private static final long serialVersionUID = 1L;
    
    /**
     * 表示空值的ValueThrowingOptional单例实例，相当于Java的Optional.empty()。
     * 该实例的{@link #get()}方法返回null，{@link #isPresent()}返回false。
     */
    static final ValueThrowingOptional<?, ?> EMPTY = new ValueThrowingOptional<>(null);

    /**
     * 构造一个封装指定值的ValueThrowingOptional实例。
     * 如果{@code value}为null，该实例行为与{@link #EMPTY}一致。
     *
     * @param value 要封装的值，可以为null
     */
    public ValueThrowingOptional(T value) {
        super(value);
    }

    /**
     * 获取封装的值，若值为null则返回null（不会抛出NoSuchElementException）。
     * 该方法直接返回封装的原始值，与父类{@link ValueThrowingSupplier#get()}行为一致。
     *
     * @return 封装的值，可能为null
     * @throws E 可能抛出的异常
     */
    @Override
    public T get() throws E {
        return ThrowingOptional.super.get();
    }

    /**
     * 对封装的值进行扁平映射转换，返回映射后的结果。
     * 若当前值为null，该方法不会执行映射函数，直接返回null。
     *
     * @param <R>    映射后的结果类型
     * @param <X>    映射函数可能抛出的异常类型
     * @param mapper 映射函数，不可为null
     * @return 映射后的结果，若当前值为null则返回null
     * @throws E 原始异常类型
     * @throws X 映射函数抛出的异常
     */
    @Override
    public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
            throws E, X {
        T value = getValue();
        return value != null ? mapper.apply(value) : null;
    }
}