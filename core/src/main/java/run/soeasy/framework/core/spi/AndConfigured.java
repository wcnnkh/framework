package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 组合配置实现类，继承自{@link AndInclude}并实现{@link ConfiguredWrapper}接口，
 * 用于将多个配置操作组合为一个可统一管理的配置实例，支持链式组合和生命周期管理。
 * <p>
 * 该类通过包装源配置实例和额外注册操作，实现配置实例的组合管理，
 * 适用于需要同时管理多个配置操作生命周期的场景，如资源注册与配置的批量管理。
 *
 * @param <S> 服务实例的类型
 * @param <W> 被包装的配置实例类型（需实现{@link Configured}）
 * 
 * @author soeasy.run
 * @see AndInclude
 * @see ConfiguredWrapper
 */
class AndConfigured<S, W extends Configured<S>> extends AndInclude<S, W> implements ConfiguredWrapper<S, W> {

    /**
     * 构造函数，创建组合配置实例
     * 
     * @param source       源配置实例，不可为null
     * @param registration 要组合的注册操作，不可为null
     * @throws NullPointerException 若source或registration为null
     */
    public AndConfigured(@NonNull W source, @NonNull Registration registration) {
        super(source, registration);
    }

    /**
     * 组合多个注册操作并返回新的Configured实例
     * <p>
     * 将当前Configured实例与新的注册操作组合，形成一个新的AndConfigured实例，
     * 新实例将管理所有已组合的注册操作的生命周期，支持链式调用。
     * 
     * @param registration 要组合的注册操作，不可为null
     * @return 组合后的Configured实例
     */
    @Override
    public Configured<S> and(Registration registration) {
        return ConfiguredWrapper.super.and(registration);
    }
}