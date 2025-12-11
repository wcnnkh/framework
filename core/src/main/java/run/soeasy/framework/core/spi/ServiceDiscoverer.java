package run.soeasy.framework.core.spi;

import java.util.ServiceLoader;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 服务发现器接口，用于根据指定类型动态发现并获取服务实例集合。
 * <p>
 * 该函数式接口定义了服务发现的核心契约，支持通过不同实现策略（如JDK SPI、Spring容器、自定义注册表等）
 * 解耦服务获取逻辑，返回的{@link Streamable}保证服务实例的只读流式访问，适配SPI多实例扩展场景。
 *
 * @author soeasy.run
 * @see Streamable
 * @see TypeServiceMap#assignableFrom(Class) （典型实现依赖）
 */
@FunctionalInterface
public interface ServiceDiscoverer {
	/**
	 * JDK原生SPI服务发现器实例（函数式实现）
	 * <p>
	 * 直接绑定{@link #load(Class)}静态方法，作为JDK SPI的默认服务发现实现，
	 * 可直接复用该实例（如{@code ServiceDiscoverer.JDK_SPI_DISCOVERER.getServices(OrderHandler.class)}），
	 * 等价于调用{@link #load(Class)}静态方法。
	 */
	static final ServiceDiscoverer JDK_SPI_DISCOVERER = ServiceDiscoverer::load;

	/**
	 * 使用当前线程的上下文类加载器加载指定服务的提供者
	 * <p>
	 * 等价于
	 * {@code ServiceLoader.load(service, Thread.currentThread().getContextClassLoader())}
	 * 
	 * @param <S>     服务接口类型
	 * @param service 服务接口或抽象类
	 * @return 服务提供者实例
	 */
	static <S> Streamable<S> load(Class<S> service) {
		ServiceLoader<S> serviceLoader = ServiceLoader.load(service);
		return Streamable.of(serviceLoader);
	}

	/**
	 * 使用指定类加载器加载指定服务的提供者
	 * 
	 * @param <S>     服务接口类型
	 * @param service 服务接口或抽象类
	 * @param loader  类加载器（null时使用系统类加载器）
	 * @return 服务提供者实例
	 */
	static <S> Streamable<S> load(Class<S> service, ClassLoader loader) {
		ServiceLoader<S> serviceLoader = ServiceLoader.load(service, loader);
		return Streamable.of(serviceLoader);
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
	static <S> Streamable<S> loadInstalled(Class<S> service) {
		ServiceLoader<S> serviceLoader = ServiceLoader.loadInstalled(service);
		return Streamable.of(serviceLoader);
	}

	/**
	 * 发现指定类型的所有服务实例
	 * <p>
	 * 根据传入的服务接口/抽象类类型，返回该类型的所有服务实例流，实现类需保证： 1.
	 * 非空校验：requiredType为null时抛出NullPointerException； 2.
	 * 类型匹配：返回的实例均为requiredType的子类/实现类； 3. 只读特性：返回的Streamable不支持修改操作。
	 * 
	 * @param <S>          服务接口/抽象类的类型
	 * @param requiredType 所需服务的接口或抽象类，不可为null
	 * @return 只读的服务实例流，不会返回null（无匹配实例时返回空Streamable）
	 * @throws NullPointerException 若requiredType为null
	 */
	<S> Streamable<S> getServices(@NonNull Class<S> requiredType);
}