package run.soeasy.framework.core.function;

import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的消费者接口，扩展了Java标准库的Consumer接口，允许在消费操作中抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的消费场景，并提供了丰富的组合操作和异常处理方法。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>异常处理：允许在accept()方法中抛出指定类型的异常E</li>
 *   <li>消费组合：提供andThen/compose方法实现消费操作的前后组合</li>
 *   <li>类型映射：通过map方法支持输入类型的转换</li>
 *   <li>异常转换：通过throwing方法实现异常类型的统一处理</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要抛出特定异常的资源处理操作（如文件写入、数据库操作）</li>
 *   <li>组合多个可能抛出异常的消费流程（如数据校验+日志记录）</li>
 *   <li>处理需要类型转换并可能抛出异常的复杂消费逻辑</li>
 *   <li>统一管理不同模块的异常类型</li>
 *   <li>资源处理完成后的回调操作注册</li>
 * </ul>
 *
 * @param <S> 消费操作的输入类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see java.util.function.Consumer
 * @see ThrowingFunction
 */
@FunctionalInterface
public interface ThrowingConsumer<S, E extends Throwable> {

    /**
     * 对迭代器中的所有元素应用消费操作。
     * 该方法递归处理迭代器中的每个元素，确保即使某个元素处理失败，也能保证已处理元素的清理操作。
     *
     * @param <S>      元素类型
     * @param <E>      异常类型
     * @param sourceIterator 输入迭代器，不可为null
     * @param consumer 消费函数，不可为null
     * @throws E 消费操作中抛出的异常
     */
    public static <S, E extends Throwable> void acceptAll(@NonNull Iterator<? extends S> sourceIterator,
            @NonNull ThrowingConsumer<? super S, ? extends E> consumer) throws E {
        if (sourceIterator.hasNext()) {
            try {
                consumer.accept(sourceIterator.next());
            } finally {
                acceptAll(sourceIterator, consumer);
            }
        }
    }

    /**
     * 获取忽略异常的ThrowingConsumer实例。
     * 该实例的accept()方法无实际操作，且不会抛出异常，适用于需要占位的场景。
     *
     * @param <S> 输入类型
     * @param <E> 异常类型
     * @return 忽略异常的ThrowingConsumer实例
     */
    @SuppressWarnings("unchecked")
    public static <S, E extends Throwable> ThrowingConsumer<S, E> ignore() {
        return (ThrowingConsumer<S, E>) IgnoreThrowingConsumer.INSTANCE;
    }

    /**
     * 对输入参数执行消费操作，可能抛出异常E。
     * 该方法是函数式接口的抽象方法，必须由实现类提供具体实现。
     *
     * @param source 输入参数
     * @throws E 可能抛出的指定类型异常
     */
    void accept(S source) throws E;

    /**
     * 组合消费操作，先执行当前消费，再执行after消费。
     * 返回的新消费会按顺序执行当前和after消费，若当前消费抛出异常则不执行after消费。
     *
     * @param after 后执行的消费，不可为null
     * @return 组合后的消费实例
     */
    default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
        return new ChainThrowingConsumer<>(ThrowingFunction.identity(), this, after, Function.identity(), ignore());
    }

    /**
     * 组合消费操作，先执行before消费，再执行当前消费。
     * 返回的新消费会按顺序执行before和当前消费，若before消费抛出异常则不执行当前消费。
     *
     * @param before 先执行的消费，不可为null
     * @return 组合后的消费实例
     */
    default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
        return new ChainThrowingConsumer<>(ThrowingFunction.identity(), before, this, Function.identity(), ignore());
    }

    /**
     * 对输入进行类型映射后再应用当前消费。
     * 返回的新消费会先通过mapper转换输入类型，再应用当前消费操作。
     *
     * @param <R>    映射后的输入类型
     * @param mapper 类型映射函数
     * @return 映射后的消费实例
     */
    default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
        return new ChainThrowingConsumer<>(mapper, this, ignore(), Function.identity(), ignore());
    }

    /**
     * 注册消费后的关闭回调。
     * 当当前消费执行完毕或抛出异常时，会调用指定的endpoint回调处理。
     *
     * @param endpoint 关闭时执行的回调消费，不可为null
     * @return 注册回调后的消费实例
     */
    default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
        return new ChainThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), Function.identity(),
                endpoint);
    }

    /**
     * 转换异常类型，返回新的ThrowingConsumer实例。
     * 该方法允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的消费实例
     */
    default <R extends Throwable> ThrowingConsumer<S, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return new ChainThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), throwingMapper, ignore());
    }
}