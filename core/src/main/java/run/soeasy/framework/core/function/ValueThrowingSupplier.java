package run.soeasy.framework.core.function;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 固定值的可抛出异常的供应者实现，用于封装一个已存在的值，
 * 每次调用{@link #get()}方法时返回该固定值，且支持抛出指定类型的异常。
 * 该类实现了{@link Serializable}接口，确保封装的值可以被序列化。
 *
 * <p>核心特性：
 * <ul>
 *   <li>不可变设计：封装的固定值在创建后不可更改</li>
 *   <li>异常支持：允许声明可能抛出的异常类型</li>
 *   <li>线程安全：由于值不可变，所有操作都是线程安全的</li>
 *   <li>序列化支持：实现Serializable接口，便于在分布式环境中使用</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>作为默认值供应者</li>
 *   <li>在函数式编程中传递已知值</li>
 *   <li>测试环境中模拟固定返回值</li>
 *   <li>需要序列化的函数式组件</li>
 * </ul>
 *
 * @param <T> 供应的值类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see ThrowingSupplier
 * @see Serializable
 */
@RequiredArgsConstructor
@Getter
class ValueThrowingSupplier<T, E extends Throwable> implements ThrowingSupplier<T, E>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 封装的固定值，在构造时初始化，不可更改。
     */
    protected final T value;

    /**
     * 返回封装的固定值，该方法不会改变内部状态，
     * 每次调用都返回相同的值，且可能抛出声明的异常类型。
     *
     * @return 封装的固定值
     * @throws E 可能抛出的异常
     */
    @Override
    public T get() throws E {
        return value;
    }
}