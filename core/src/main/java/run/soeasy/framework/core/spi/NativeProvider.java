package run.soeasy.framework.core.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Provider;

/**
 * JDK原生服务提供者实现，封装{@link ServiceLoader}实现服务发现与加载功能。
 * <p>
 * 该实现基于Java SPI（Service Provider Interface）机制，提供对服务提供者的统一访问接口，
 * 支持通过类加载器动态发现和加载服务实现类，适用于需要解耦服务接口与实现的场景。
 *
 * @param <S> 服务接口类型
 * 
 * @author soeasy.run
 * @see ServiceLoader
 * @see Provider
 */
@RequiredArgsConstructor
public class NativeProvider<S> implements Provider<S> {
    /**
     * 使用当前线程的上下文类加载器加载指定服务的提供者
     * <p>
     * 等价于 {@code ServiceLoader.load(service, Thread.currentThread().getContextClassLoader())}
     * 
     * @param <S>     服务接口类型
     * @param service 服务接口或抽象类
     * @return 服务提供者实例
     */
    public static <S> Provider<S> load(Class<S> service) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(service);
        return new NativeProvider<>(serviceLoader);
    }

    /**
     * 使用指定类加载器加载指定服务的提供者
     * 
     * @param <S>     服务接口类型
     * @param service 服务接口或抽象类
     * @param loader  类加载器（null时使用系统类加载器）
     * @return 服务提供者实例
     */
    public static <S> Provider<S> load(Class<S> service, ClassLoader loader) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(service, loader);
        return new NativeProvider<>(serviceLoader);
    }

    /**
     * 使用扩展类加载器加载已安装的服务提供者
     * <p>
     * 该方法仅加载JVM安装的服务提供者，忽略应用类路径中的实现
     * 
     * @param <S>     服务接口类型
     * @param service 服务接口或抽象类
     * @return 服务提供者实例
     */
    public static <S> Provider<S> loadInstalled(Class<S> service) {
        ServiceLoader<S> serviceLoader = ServiceLoader.loadInstalled(service);
        return new NativeProvider<>(serviceLoader);
    }

    /** 封装的JDK原生ServiceLoader实例 */
    private final ServiceLoader<S> serviceLoader;

    /**
     * 获取服务提供者迭代器
     * <p>
     * 迭代器遍历顺序依赖于服务提供者的配置顺序和类加载器行为
     * 
     * @return 服务实例迭代器
     */
    @Override
    public Iterator<S> iterator() {
        return serviceLoader.iterator();
    }

    /**
     * 重新加载服务提供者
     * <p>
     * 清空已加载的服务提供者缓存，重新扫描服务配置文件
     */
    @Override
    public void reload() {
        serviceLoader.reload();
    }
}