package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 系统级服务提供者工厂，基于单例模式实现，整合配置化服务发现与JDK原生SPI机制。
 * <p>
 * 该工厂继承自{@link ConfigurableProviderFactory}，优先使用配置的服务发现策略，
 * 若未找到则回退到JDK原生{@link NativeProvider}实现，确保服务发现的兼容性和可扩展性。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>单例模式：通过双重检查锁定确保全局唯一实例，保证线程安全</li>
 *   <li>优先级发现：先查配置的服务提供者工厂链，再使用JDK原生SPI机制</li>
 *   <li>自动初始化：实例化时自动注册{@link ProviderFactory}的原生发现器</li>
 *   <li>空值防御：确保返回的{@link Provider}实例非null，避免NPE风险</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ConfigurableProviderFactory
 * @see NativeProvider
 */
public final class SystemProviderFactory extends ConfigurableProviderFactory {
    /**
     * 系统提供者工厂单例实例
     * <p>
     * 使用volatile关键字保证多线程环境下的可见性，
     * 禁止指令重排序，确保实例初始化的完整性
     */
    private static volatile SystemProviderFactory instance;

    /** 私有构造函数，禁止外部通过new创建实例，强制使用单例模式 */
    private SystemProviderFactory() {
    }

    /**
     * 获取系统提供者工厂的全局唯一实例
     * <p>
     * 实现双重检查锁定（DCL）模式，确保线程安全的单例创建：
     * <ol>
     *   <li>首次调用时检查instance是否为null（第一层检查，避免无意义加锁）</li>
     *   <li>加类锁后再次检查（第二层检查，解决多线程并发创建问题）</li>
     *   <li>实例化后自动注册{@link ProviderFactory}的原生发现器</li>
     * </ol>
     *
     * @return 系统提供者工厂的单例实例
     */
    public static SystemProviderFactory getInstance() {
        if (instance == null) { // 第一层空检查
            synchronized (SystemProviderFactory.class) { // 加类锁
                if (instance == null) { // 第二层空检查
                    instance = new SystemProviderFactory();
                    // 初始化时自动注册JDK原生的ProviderFactory发现器
                    instance.registers(NativeProvider.load(ProviderFactory.class));
                }
            }
        }
        return instance;
    }

    /**
     * 获取指定类型的服务提供者（支持优先级发现策略）
     * <p>
     * 服务发现执行流程：
     * <ol>
     *   <li>先调用父类{@link ConfigurableProviderFactory#getProvider}，遍历配置的工厂链</li>
     *   <li>若配置的工厂链未找到，则回退到JDK原生{@link NativeProvider#load}机制</li>
     *   <li>通过双重检查确保返回的Provider实例非null</li>
     * </ol>
     *
     * @param <S>          服务接口的泛型类型
     * @param requiredType 所需服务的接口或抽象类，不可为null
     * @return 服务提供者实例（保证非null）
     * @throws NullPointerException 若requiredType为null
     */
    @Override
    public <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
        // 优先从配置的工厂链中获取服务提供者
        Provider<S> provider = super.getProvider(requiredType);
        // 配置未找到时，回退到JDK原生SPI机制
        return provider != null ? provider : NativeProvider.load(requiredType);
    }
}