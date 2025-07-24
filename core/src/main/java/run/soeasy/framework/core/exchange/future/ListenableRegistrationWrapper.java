package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 可监听注册包装器接口，定义对可监听注册实例的包装器模式实现。
 * 该接口继承自{@link ListenableRegistration}和{@link RegistrationWrapper}，
 * 允许在不修改原有注册实例的情况下增强其功能，如添加日志记录、
 * 条件判断或结果转换等。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有操作委派给源注册实例</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>类型安全：通过泛型参数确保包装对象类型一致性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加注册操作的日志记录功能</li>
 *   <li>实现注册操作的条件性状态判断</li>
 *   <li>添加注册结果的转换或增强</li>
 *   <li>实现注册操作的事务性保障</li>
 * </ul>
 *
 * @param <T> 注册操作返回的回执类型，需实现{@link Receipt}接口
 * @param <W> 包装器自身的类型，需实现当前接口
 * 
 * @author soeasy.run
 * @see ListenableRegistration
 * @see RegistrationWrapper
 */
@FunctionalInterface
public interface ListenableRegistrationWrapper<T extends Receipt, W extends ListenableRegistration<T>>
        extends ListenableRegistration<T>, RegistrationWrapper<W> {

    /**
     * 注册监听器到源注册实例
     * 默认实现将操作委派给源注册实例
     * 
     * @param listener 待注册的监听器
     * @return 注册操作的回执，用于取消注册
     */
    @Override
    default Registration registerListener(Listener<T> listener) {
        return getSource().registerListener(listener);
    }

    /**
     * 注册完成事件监听器到源注册实例
     * 默认实现将操作委派给源注册实例
     * 
     * @param listener 完成事件监听器
     * @return 当前可监听注册包装器实例，支持链式调用
     */
    @Override
    default ListenableRegistration<T> onComplete(Listener<? super T> listener) {
        return getSource().onComplete(listener);
    }

    /**
     * 注册失败事件监听器到源注册实例
     * 默认实现将操作委派给源注册实例
     * 
     * @param listener 失败事件监听器
     * @return 当前可监听注册包装器实例，支持链式调用
     */
    @Override
    default ListenableRegistration<T> onFailure(Listener<? super T> listener) {
        return getSource().onFailure(listener);
    }

    /**
     * 注册成功事件监听器到源注册实例
     * 默认实现将操作委派给源注册实例
     * 
     * @param listener 成功事件监听器
     * @return 当前可监听注册包装器实例，支持链式调用
     */
    @Override
    default ListenableRegistration<T> onSuccess(Listener<? super T> listener) {
        return getSource().onSuccess(listener);
    }
}