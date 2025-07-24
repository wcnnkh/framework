package run.soeasy.framework.core.exchange;

import java.util.List;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

/**
 * 注册操作集合接口，用于批量管理多个注册操作。
 * 该接口继承自{@link Registration}和{@link Listable}，
 * 允许将多个注册操作视为一个整体进行统一管理。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量操作：支持批量取消、状态查询等操作</li>
 *   <li>不可变设计：实例创建后元素不可修改</li>
 *   <li>空安全：提供空实现和工厂方法确保安全使用</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>资源注册的统一管理与释放</li>
 *   <li>组件生命周期的批量控制</li>
 *   <li>事件监听器的统一注册与注销</li>
 *   <li>服务注册的批量管理</li>
 * </ul>
 *
 * @param <R> 注册操作的类型，需实现{@link Registration}接口
 * 
 * @author soeasy.run
 * @see Registration
 * @see Listable
 * @see Elements
 */
@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration, Listable<R> {
    /**
     * 获取空的注册操作集合
     * 
     * @param <E> 注册操作类型
     * @return 空的注册操作集合实例
     */
    @SuppressWarnings("unchecked")
    public static <E extends Registration> Registrations<E> empty() {
        return (Registrations<E>) EmptyRegistrations.INSTANCE;
    }

    /**
     * 从元素集合创建注册操作集合
     * 
     * @param <E> 注册操作类型
     * @param elements 注册操作元素集合
     * @return 包含指定注册操作的集合，若elements为null则返回空集合
     */
    public static <E extends Registration> Registrations<E> forElements(Elements<E> elements) {
        if (elements == null) {
            return empty();
        }
        return () -> elements;
    }

    /**
     * 从列表创建注册操作集合
     * 
     * @param <E> 注册操作类型
     * @param list 注册操作列表
     * @return 包含指定注册操作的集合，若list为null则返回空集合
     */
    public static <E extends Registration> Registrations<E> forList(List<? extends E> list) {
        if (list == null) {
            return empty();
        }

        return forElements(Elements.of(list));
    }

    /**
     * 批量取消所有注册操作
     * 
     * @return 如果所有可取消的注册操作都成功取消返回true，否则返回false
     */
    @Override
    default boolean cancel() {
        if (hasElements()) {
            return false;
        }

        for (R registration : getElements()) {
            if (registration.isCancellable()) {
                if (!registration.cancel()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断集合中是否存在可取消的注册操作
     * 
     * @return 如果集合中存在至少一个可取消的注册操作返回true，否则返回false
     */
    @Override
    default boolean isCancellable() {
        return hasElements() ? false : getElements().anyMatch((e) -> e.isCancellable());
    }

    /**
     * 判断集合中所有注册操作是否都已取消
     * 
     * @return 如果集合中所有注册操作都已取消返回true，否则返回false
     */
    @Override
    default boolean isCancelled() {
        return hasElements() ? false : getElements().allMatch((e) -> e.isCancelled());
    }
}