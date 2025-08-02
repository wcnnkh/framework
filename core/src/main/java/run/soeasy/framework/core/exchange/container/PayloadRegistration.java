package run.soeasy.framework.core.exchange.container;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 带有效载荷的注册接口，继承自Registration并添加载荷数据功能。
 * <p>
 * 该接口允许注册操作携带有效载荷（Payload），适用于需要在注册过程中传递数据的场景。
 * 提供了便捷的静态工厂方法创建成功/失败的注册实例，并支持载荷映射和注册组合操作。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>载荷数据携带：通过{@link #getPayload()}获取注册相关的载荷数据</li>
 *   <li>注册状态标记：预定义{@link #FAILURE}和{@link #SUCCESS}常量</li>
 *   <li>函数式映射：通过{@link #map(Function)}转换载荷类型</li>
 *   <li>注册组合：通过{@link #and(Registration)}组合多个注册操作</li>
 * </ul>
 *
 * @param <T> 载荷数据的类型
 * 
 * @author soeasy.run
 * @see Registration
 */
public interface PayloadRegistration<T> extends Registration {
    /**
     * 创建注册失败的实例（载荷为null）
     * 
     * @param <E> 载荷类型
     * @return 失败的PayloadRegisted实例
     */
    @SuppressWarnings("unchecked")
    public static <E> PayloadRegisted<E> failure() {
        return (PayloadRegisted<E>) PayloadRegisted.FAILURE;
    }

    /**
     * 创建注册失败的实例（指定载荷）
     * 
     * @param <E> 载荷类型
     * @param payload 失败时的载荷数据
     * @return 失败的PayloadRegisted实例
     */
    public static <E> PayloadRegisted<E> failure(E payload) {
        return new PayloadRegisted<E>(true, payload);
    }

    /**
     * 创建注册成功的实例（载荷为null）
     * 
     * @param <E> 载荷类型
     * @return 成功的PayloadRegisted实例
     */
    @SuppressWarnings("unchecked")
    public static <E> PayloadRegisted<E> success() {
        return (PayloadRegisted<E>) PayloadRegisted.SUCCESS;
    }

    /**
     * 创建注册成功的实例（指定载荷）
     * 
     * @param <E> 载荷类型
     * @param payload 成功时的载荷数据
     * @return 成功的PayloadRegisted实例
     */
    public static <E> PayloadRegisted<E> success(E payload) {
        return new PayloadRegisted<E>(false, payload);
    }

    /**
     * 获取注册的载荷数据
     * 
     * @return 注册携带的载荷数据
     */
    T getPayload();

    /**
     * 对载荷数据进行映射转换
     * <p>
     * 通过Function将当前载荷类型T转换为R类型，返回新的PayloadRegistration实例
     * 
     * @param <R> 目标载荷类型
     * @param mapper 载荷转换函数，不可为null
     * @return 转换后的PayloadRegistration实例
     * @throws NullPointerException 若mapper为null
     */
    default <R> PayloadRegistration<R> map(@NonNull Function<? super T, ? extends R> mapper) {
        return new MappedPayloadRegistration<>(this, mapper);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 当另一个注册为null或已取消时，直接返回当前注册；否则返回组合后的注册
     * 
     * @param registration 要组合的注册
     * @return 组合后的PayloadRegistration实例
     */
    @Override
    default PayloadRegistration<T> and(Registration registration) {
        if (registration == null || registration.isCancelled()) {
            return this;
        }
        return new PayloadRegistrationWrapped<>(this, Elements.singleton(registration));
    }
}