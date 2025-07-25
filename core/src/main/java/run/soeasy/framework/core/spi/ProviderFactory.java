package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 服务提供者工厂接口，用于创建指定类型的服务提供者实例。
 * <p>
 * 该函数式接口定义了根据服务类型创建对应{@link Provider}的契约，
 * 支持通过不同实现策略（如JDK SPI、Spring容器等）动态获取服务实例，
 * 适用于需要解耦服务获取逻辑的场景。
 *
 * @author soeasy.run
 * @see Provider
 */
@FunctionalInterface
public interface ProviderFactory {
    /**
     * 创建指定类型的服务提供者
     * <p>
     * 根据传入的服务接口类型创建对应的{@link Provider}实例，
     * 实现类需确保返回的提供者能正确提供该类型的服务实例。
     * 
     * @param <S>          服务接口的类型
     * @param requiredType 所需服务的接口或抽象类，不可为null
     * @return 服务提供者实例，用于获取指定类型的服务实例
     * @throws NullPointerException 若requiredType为null
     */
    <S> Provider<S> getProvider(@NonNull Class<S> requiredType);
}