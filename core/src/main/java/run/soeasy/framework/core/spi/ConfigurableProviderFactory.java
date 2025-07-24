package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 可配置的服务提供者工厂，管理多个服务提供者工厂实例，实现服务发现的链式查找。
 * <p>
 * 该类继承自{@link Services}，将多个{@link ProviderFactory}组合为一个复合工厂，
 * 实现服务提供者的链式查找，适用于需要整合多种服务发现机制的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>链式查找：按注册顺序依次尝试每个工厂，直到找到匹配的服务提供者</li>
 *   <li>动态配置：支持运行时添加、移除服务提供者工厂</li>
 *   <li>空值安全：若所有工厂均未找到服务提供者，返回null</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Services
 * @see ProviderFactory
 */
public class ConfigurableProviderFactory extends Services<ProviderFactory> implements ProviderFactory {

    /**
     * 获取指定类型的服务提供者
     * <p>
     * 按注册顺序遍历所有服务提供者工厂，调用其{@link ProviderFactory#getProvider}方法，
     * 一旦找到非空的服务提供者立即返回，否则返回null。
     * 
     * @param <S>          服务接口的类型
     * @param requiredType 所需服务的接口或抽象类，不可为null
     * @return 第一个匹配的服务提供者，若未找到则返回null
     * @throws NullPointerException 若requiredType为null
     */
    @Override
    public <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
        for(ProviderFactory factory : this) {
            Provider<S> provider = factory.getProvider(requiredType);
            if(provider != null) {
                return provider;
            }
        }
        return null;
    }
}