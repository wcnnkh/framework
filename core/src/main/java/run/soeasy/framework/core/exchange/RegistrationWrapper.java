package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 注册操作包装器接口，定义对基础注册操作的装饰器模式实现。
 * 该接口继承自{@link Registration}和{@link Wrapper}，
 * 允许在不修改原有注册操作的情况下增强其功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明代理：默认实现将所有状态查询和取消操作委派给源注册操作</li>
 *   <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 *   <li>类型安全：通过泛型参数确保包装对象类型一致性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>添加注册操作的日志记录功能</li>
 *   <li>实现注册操作的条件性取消逻辑</li>
 *   <li>添加注册操作的事务管理</li>
 *   <li>实现注册操作的监控和统计</li>
 * </ul>
 *
 * @param <W> 被包装的基础注册操作类型
 * 
 * @author soeasy.run
 * @see Registration
 * @see Wrapper
 */
@FunctionalInterface
public interface RegistrationWrapper<W extends Registration> extends Registration, Wrapper<W> {

    /**
     * 判断被包装的注册操作是否可取消
     * 默认实现将操作委派给源注册操作
     * 
     * @return 如果源注册操作可取消返回true，否则返回false
     */
    @Override
    default boolean isCancellable() {
        return getSource().isCancellable();
    }

    /**
     * 尝试取消被包装的注册操作
     * 默认实现将操作委派给源注册操作
     * 
     * @return 如果取消操作成功返回true，否则返回false
     */
    @Override
    default boolean cancel() {
        return getSource().cancel();
    }

    /**
     * 判断被包装的注册操作是否已取消
     * 默认实现将操作委派给源注册操作
     * 
     * @return 如果源注册操作已取消返回true，否则返回false
     */
    @Override
    default boolean isCancelled() {
        return getSource().isCancelled();
    }
}