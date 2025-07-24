package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.ListableWrapper;

/**
 * 注册操作集合包装器接口，定义对基础注册操作集合的装饰器模式实现。
 * 该接口继承自{@link Registrations}、{@link RegistrationWrapper}和{@link ListableWrapper}，
 * 允许在不修改原有注册操作集合的情况下增强其功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有操作委派给源注册操作集合</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>批量操作支持：通过包装集合实现批量注册操作的统一管理</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加批量注册操作的拦截和验证逻辑</li>
 *   <li>实现注册操作集合的事务性保障</li>
 *   <li>添加注册操作集合的监控和统计功能</li>
 *   <li>实现注册操作集合的条件性访问控制</li>
 * </ul>
 *
 * @param <R> 注册操作的类型，需实现{@link Registration}接口
 * @param <W> 被包装的基础注册操作集合类型
 * 
 * @author soeasy.run
 * @see Registrations
 * @see RegistrationWrapper
 * @see ListableWrapper
 */
@FunctionalInterface
public interface RegistrationsWrapper<R extends Registration, W extends Registrations<R>>
        extends Registrations<R>, RegistrationWrapper<W>, ListableWrapper<R, W> {

    /**
     * 批量取消所有注册操作
     * 默认实现将操作委派给源注册操作集合
     * 
     * @return 如果所有可取消的注册操作都成功取消返回true，否则返回false
     */
    @Override
    default boolean cancel() {
        return getSource().cancel();
    }

    /**
     * 判断集合中是否存在可取消的注册操作
     * 默认实现将操作委派给源注册操作集合
     * 
     * @return 如果集合中存在至少一个可取消的注册操作返回true，否则返回false
     */
    @Override
    default boolean isCancellable() {
        return getSource().isCancellable();
    }

    /**
     * 判断集合中所有注册操作是否都已取消
     * 默认实现将操作委派给源注册操作集合
     * 
     * @return 如果集合中所有注册操作都已取消返回true，否则返回false
     */
    @Override
    default boolean isCancelled() {
        return getSource().isCancelled();
    }
}