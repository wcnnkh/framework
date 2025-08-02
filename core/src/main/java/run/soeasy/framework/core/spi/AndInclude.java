package run.soeasy.framework.core.spi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 组合包含实现类，将多个注册操作组合为一个可管理的包含实例，实现{@link IncludeWrapper}接口。
 * <p>
 * 该类通过包装源包含实例和额外注册操作，实现包含实例的链式组合，
 * 支持统一管理多个注册操作的生命周期，适用于需要组合多个资源注册的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>组合注册：将多个注册操作合并为单一实例，统一管理</li>
 *   <li>链式调用：通过{@link #and}方法支持无限级联组合注册</li>
 *   <li>状态聚合：聚合多个注册操作的可取消性和取消状态</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * @param <W> 被包装的包含实例类型
 * 
 * @author soeasy.run
 * @see IncludeWrapper
 */
@Getter
@RequiredArgsConstructor
class AndInclude<S, W extends Include<S>> implements IncludeWrapper<S, W> {
    
    /** 被包装的源包含实例，提供基础服务实例集合 */
    @NonNull
    private final W source;
    
    /** 要组合的注册操作，与源包含实例的生命周期关联 */
    @NonNull
    private final Registration registration;

    /**
     * 组合多个注册操作并返回新的包含实例
     * <p>
     * 将当前包含实例与新的注册操作组合，形成一个新的AndInclude实例，
     * 新实例将管理所有已组合的注册操作的生命周期。
     * 
     * @param registration 要组合的注册操作，不可为null
     * @return 组合后的包含实例
     */
    @Override
    public Include<S> and(Registration registration) {
        return new AndInclude<>(source, this.registration.and(registration));
    }

    /**
     * 取消所有组合的注册操作
     * <p>
     * 依次调用源包含实例和所有组合注册操作的取消方法，
     * 只有当所有操作都成功取消时才返回true。
     * 
     * @return 所有注册操作是否都成功取消
     */
    @Override
    public boolean cancel() {
        return IncludeWrapper.super.cancel() && registration.cancel();
    }

    /**
     * 判断是否至少有一个可取消的注册操作
     * <p>
     * 只要源包含实例或任意一个组合注册操作可取消，即返回true。
     * 
     * @return 是否存在可取消的注册操作
     */
    @Override
    public boolean isCancellable() {
        return IncludeWrapper.super.isCancellable() || registration.isCancellable();
    }

    /**
     * 判断所有注册操作是否都已取消
     * <p>
     * 只有当源包含实例和所有组合注册操作都已取消时才返回true。
     * 
     * @return 是否所有注册操作都已取消
     */
    @Override
    public boolean isCancelled() {
        return IncludeWrapper.super.isCancelled() && registration.isCancelled();
    }
}