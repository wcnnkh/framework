package run.soeasy.framework.core.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.comparator.OrderWrapped;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 可配置的服务容器，支持通过多种服务发现机制动态加载服务实现，实现{@link Configurable}接口。
 * <p>
 * 该类继承自{@link Services}，提供基于{@link ProviderFactory}的服务发现配置能力，
 * 支持动态注册、卸载和重新加载服务提供者，并通过内部锁机制保证线程安全，
 * 适用于需要灵活配置服务发现策略的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>多源发现：支持通过多个{@link ProviderFactory}同时发现服务</li>
 *   <li>配置管理：通过{@link Configured}接口返回配置结果，支持状态查询和操作</li>
 *   <li>顺序控制：使用{@link OrderWrapped}对服务进行排序包装，确保有序加载</li>
 *   <li>动态刷新：支持服务的重新加载，无需重启容器</li>
 * </ul>
 *
 * @param <S> 服务接口的类型
 * 
 * @author soeasy.run
 * @see Services
 * @see Configurable
 * @see ProviderFactory
 */
public class ConfigurableServices<S> extends Services<S> implements Configurable {
    
    /**
     * 配置实现类，封装服务发现配置的结果，实现{@link Configured}接口。
     * <p>
     * 该类负责管理单个{@link ProviderFactory}的配置状态，包括注册、取消注册和状态查询。
     * </p>
     */
    @RequiredArgsConstructor
    private final class Configuration
            implements Configured<OrderWrapped<S>>, IncludeWrapper<OrderWrapped<S>, Include<OrderWrapped<S>>> {
        
        private final Receipt receipt;                // 配置结果收据
        private final ProviderFactory serviceLoaderDiscovery;  // 服务发现工厂
        private final Include<OrderWrapped<S>> source;  // 源包含实例

        /**
         * 取消配置注册
         * <p>
         * 从发现映射中移除对应的服务发现配置，并调用源包含实例的取消方法。
         * 
         * @return 取消操作是否成功
         */
        @Override
        public boolean cancel() {
            Lock lock = getContainer().writeLock();
            lock.lock();
            try {
                if (discoveryMap == null) {
                    return false;
                }

                if (discoveryMap.remove(serviceLoaderDiscovery) != null) {
                    return IncludeWrapper.super.cancel();
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 获取配置失败原因
         * 
         * @return 失败原因（成功时为null）
         */
        @Override
        public Throwable cause() {
            return receipt.cause();
        }

        /**
         * 获取源包含实例
         * 
         * @return 源包含实例
         */
        @Override
        public Include<OrderWrapped<S>> getSource() {
            return source;
        }

        /**
         * 判断配置是否已取消
         * 
         * @return true表示已取消，false表示未取消
         */
        @Override
        public boolean isCancelled() {
            Lock lock = getContainer().readLock();
            lock.lock();
            try {
                return discoveryMap == null ? false : !discoveryMap.containsKey(serviceLoaderDiscovery);
            } finally {
                lock.unlock();
            }
        }

        /**
         * 判断配置是否已完成
         * 
         * @return true表示已完成，false表示未完成
         */
        @Override
        public boolean isDone() {
            return receipt.isDone();
        }

        /**
         * 判断配置是否成功
         * 
         * @return true表示成功，false表示失败
         */
        @Override
        public boolean isSuccess() {
            return receipt.isSuccess();
        }

        /**
         * 组合多个注册操作
         * 
         * @param registration 要组合的注册操作
         * @return 组合后的Configured实例
         */
        @Override
        public Configured<OrderWrapped<S>> and(Registration registration) {
            return Configured.super.and(registration);
        }

        /**
         * 转换服务实例流
         * 
         * @param <U>       转换后的服务实例类型
         * @param resize    是否调整结果大小
         * @param converter 转换函数
         * @return 转换后的Configured实例
         */
        @Override
        public <U> Configured<U> map(boolean resize,
                @NonNull Function<? super Stream<OrderWrapped<S>>, ? extends Stream<U>> converter) {
            return Configured.super.map(resize, converter);
        }
    }

    private volatile Map<ProviderFactory, Include<OrderWrapped<S>>> discoveryMap;  // 服务发现映射
    private volatile Class<? extends S> serviceClass;  // 服务接口类型

    /**
     * 使用指定的服务发现工厂配置服务
     * 
     * @param discovery 服务发现工厂
     * @return 配置结果收据
     */
    @Override
    public Receipt configure(ProviderFactory discovery) {
        return configure(discovery, true);
    }

    /**
     * 使用指定的服务发现工厂配置服务，并可选择是否支持重新加载
     * 
     * @param discovery  服务发现工厂
     * @param reloadable 是否支持重新加载
     * @return 配置结果
     */
    public Configured<OrderWrapped<S>> configure(ProviderFactory discovery, boolean reloadable) {
        Lock lock = getContainer().writeLock();
        lock.lock();
        try {
            if (serviceClass == null) {
                return Configured.failure();
            }

            Provider<? extends S> serviceLoader = discovery.getProvider(serviceClass);
            if (serviceLoader == null) {
                return Configured.failure();
            }

            return configure(discovery, serviceLoader, reloadable);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 内部配置方法，执行实际的服务注册和发现映射维护
     * 
     * @param discovery    服务发现工厂
     * @param serviceLoader 服务加载器
     * @param reloadable   是否支持重新加载
     * @return 配置结果
     */
    private Configuration configure(ProviderFactory discovery, Provider<? extends S> serviceLoader,
            boolean reloadable) {
        if (discoveryMap == null) {
            discoveryMap = new HashMap<>(2, 1);
        }

        Include<OrderWrapped<S>> include = discoveryMap.get(discovery);
        if (include == null) {
            include = registers(serviceLoader);
            discoveryMap.put(discovery, include);
        } else if (reloadable) {
            include.reload();
        }
        return new Configuration(Receipt.SUCCESS, discovery, include);
    }

    /**
     * 获取服务接口类型
     * 
     * @return 服务接口类型
     */
    public Class<? extends S> getServiceClass() {
        Lock lock = getContainer().readLock();
        lock.lock();
        try {
            return serviceClass;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置服务接口类型
     * 
     * @param serviceClass 服务接口类型
     */
    public void setServiceClass(Class<? extends S> serviceClass) {
        Lock lock = getContainer().writeLock();
        lock.lock();
        try {
            if (this.serviceClass == serviceClass) {
                return;
            }

            this.serviceClass = serviceClass;
            // 如果已经初始化了需要reload
        } finally {
            lock.unlock();
        }
    }
}