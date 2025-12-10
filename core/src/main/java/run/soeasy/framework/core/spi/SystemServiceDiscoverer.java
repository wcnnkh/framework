package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 系统级单例复合服务发现器，整合配置化服务发现链与JDK原生SPI机制的全局统一入口。
 * <p>
 * 该类继承自{@link CompositeServiceDiscoverer}（复合服务发现器），采用「单例模式+双重检查锁定」保证全局唯一，
 * 核心策略为「配置化发现器优先，JDK SPI兜底」：先通过配置的服务发现器链查找服务，未找到则回退到JDK原生SPI机制，
 * 全程保证返回非null的{@link Streamable}实例，避免空指针风险。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>单例安全：基于双重检查锁定（DCL）+ volatile保证多线程下实例唯一且初始化完整，禁止外部实例化</li>
 * <li>优先级发现：先遍历配置的{@link ServiceDiscoverer}链（父类能力），无结果则回退到JDK原生SPI</li>
 * <li>自动初始化：单例实例化时，自动注册通过JDK SPI加载的{@link ServiceDiscoverer}实现类</li>
 * <li>空值防御：无论是否找到服务，均返回非null的{@link Streamable}（空结果返回空Streamable）</li>
 * <li>全局统一：作为系统级服务发现的唯一入口，整合多源服务发现能力（配置化+原生SPI）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see CompositeServiceDiscoverer 复合服务发现器（父类，提供多发现器管理能力）
 * @see ServiceDiscoverer 基础服务发现器接口（定义服务查找契约）
 * @since 1.0.0
 */
public final class SystemServiceDiscoverer extends CompositeServiceDiscoverer {
	/**
	 * 全局唯一单例实例
	 * <p>
	 * volatile关键字作用： 1. 保证多线程环境下实例的可见性（一个线程修改后其他线程立即可见）； 2.
	 * 禁止指令重排序，确保实例初始化完成后才被其他线程访问（避免DCL失效）。
	 */
	private static volatile SystemServiceDiscoverer instance;

	/**
	 * 私有构造函数：禁止外部通过new关键字实例化，强制通过{#getInstance()}获取单例
	 * <p>
	 * 构造过程仅在单例首次初始化时执行，无额外逻辑（初始化逻辑集中在{#getInstance()}）
	 */
	private SystemServiceDiscoverer() {
	}

	/**
	 * 获取系统级服务发现器的全局唯一单例实例
	 * <p>
	 * 实现「双重检查锁定（DCL）」模式，兼顾性能与线程安全：
	 * <ol>
	 * <li>第一层空检查：避免每次调用都加锁（提升高并发场景性能）；</li>
	 * <li>类级锁：保证多线程下仅一个线程执行实例化逻辑；</li>
	 * <li>第二层空检查：防止锁等待期间已有线程完成实例化；</li>
	 * <li>自动初始化：实例化后立即通过JDK SPI加载并注册{@link ServiceDiscoverer}实现类</li>
	 * </ol>
	 *
	 * @return 全局唯一的{@link SystemServiceDiscoverer}单例实例（永不返回null）
	 */
	public static SystemServiceDiscoverer getInstance() {
		if (instance == null) { // 第一层空检查：无锁快速路径
			synchronized (SystemServiceDiscoverer.class) { // 类锁：保证实例化逻辑互斥
				if (instance == null) { // 第二层空检查：防止并发实例化
					instance = new SystemServiceDiscoverer();
					// 初始化：通过JDK SPI加载ServiceDiscoverer实现类并批量注册到当前实例
					instance.registerAll(ServiceDiscoverer.load(ServiceDiscoverer.class));
				}
			}
		}
		return instance;
	}

	/**
	 * 查找指定类型的服务实例流（核心服务发现方法，实现优先级兜底策略）
	 * <p>
	 * 服务发现执行流程（短路逻辑，找到非空结果立即返回）：
	 * <ol>
	 * <li>优先策略：调用父类{@link CompositeServiceDiscoverer#getServices}，遍历已配置的服务发现器链；</li>
	 * <li>兜底策略：若配置的发现器链无匹配结果（返回空Streamable），则通过JDK原生SPI（{@link ServiceDiscoverer#load}）查找；</li>
	 * <li>空值保障：最终返回非null的{@link Streamable}（无匹配服务时返回空Streamable）</li>
	 * </ol>
	 *
	 * @param <S>          服务接口/抽象类的泛型类型
	 * @param requiredType 所需服务的接口或抽象类（不可为null），仅匹配该类型的子类/实现类实例
	 * @return 非null的服务实例流：
	 *         <ul>
	 *         <li>有匹配服务：返回包含服务实例的Streamable；</li>
	 *         <li>无匹配服务：返回空的Streamable（而非null）</li>
	 *         </ul>
	 * @throws NullPointerException 若requiredType为null（由lombok @NonNull注解强制校验）
	 * @see ServiceDiscoverer#load(Class) JDK原生SPI服务加载方法（兜底策略核心）
	 */
	@Override
	public <S> Streamable<S> getServices(@NonNull Class<S> requiredType) {
		// 优先从配置的复合发现器链中查找服务
		Streamable<S> services = super.getServices(requiredType);
		// 兜底：若配置链无结果，使用JDK SPI加载（保证返回非null）
		return services.isEmpty() ? ServiceDiscoverer.load(requiredType) : services;
	}
}