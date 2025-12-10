package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Operation;

/**
 * 可配置接口，定义通过服务发现工厂进行配置的能力。
 * <p>
 * 实现该接口的类可通过{@link ServiceDiscoverer}动态配置服务发现机制，
 * 支持自定义配置和系统默认配置两种模式，适用于需要灵活加载服务实现的组件。
 *
 * @author soeasy.run
 */
public interface Configurable {

    /**
     * 使用指定的服务发现工厂进行配置
     * <p>
     * 该方法将指定的{@link ServiceDiscoverer}应用于实现类，
     * 实现服务提供者的动态注册与加载，返回配置操作的结果状态。
     * 
     * @param discovery 服务发现工厂，不可为null
     * @return 配置操作的结果收据，包含成功/失败状态及可能的异常信息
     * @throws NullPointerException 若discovery为null
     */
    Operation configure(@NonNull ServiceDiscoverer discovery);

    /**
     * 使用系统默认的服务发现工厂进行配置
     * <p>
     * 该方法是对{@link #configure(ServiceDiscoverer)}的便捷调用，
     * 默认使用{@link SystemServiceDiscoverer}作为服务发现工厂，
     * 适用于大多数需要统一配置策略的场景。
     * 
     * @return 配置操作的结果收据
     */
    default Operation configure() {
        return configure(SystemServiceDiscoverer.getInstance());
    }
}