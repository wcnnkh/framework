package run.soeasy.framework.core.spi;

import java.util.Comparator;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 可配置的服务容器，继承{@link Services}获得服务实例的注册/注入能力，实现{@link Configurable}接口提供标准化配置能力。
 * <p>
 * 核心特性：
 * <ul>
 * <li>泛型自动解析：自动解析当前类的泛型参数作为默认服务类（支持手动覆盖）；</li>
 * <li>线程安全配置：基于双重检查锁定（DCL）保证配置操作的线程安全，仅首次配置/配置失败时重新执行；</li>
 * <li>配置结果缓存：缓存配置操作结果（{@link Operation}），避免重复配置；</li>
 * <li>空值防御：全链路非空校验，精准异常提示，避免空指针风险。</li>
 * </ul>
 *
 * @param <S> 服务接口/抽象类的泛型类型
 * @author soeasy.run
 * @see Services 基础服务容器（提供注册/注入能力）
 * @see Configurable 可配置接口（定义标准化配置契约）
 * @see ServiceDiscoverer 服务发现器（配置时依赖其获取服务实例）
 */
public class ConfigurableServices<S> extends Services<S> implements Configurable {

	/**
	 * 目标服务类（泛型对应的实际类型）
	 * <p>
	 * volatile保证多线程下的可见性，支持自动解析（泛型）和手动设置两种方式
	 */
	private volatile Class<? extends S> serviceClass;

	/**
	 * 配置操作结果（缓存）
	 * <p>
	 * volatile保证多线程下配置结果的可见性，避免重复执行配置逻辑；
	 * 命名优化：从原`operation`改为`configureOperation`，明确是「配置操作」的结果，而非通用操作
	 */
	private volatile Operation configureOperation;

	public ConfigurableServices(@NonNull Comparator<? super S> comparator) {
		super(comparator);
	}

	/**
	 * 获取目标服务类（自动解析泛型+双重检查锁定）
	 * <p>
	 * 优先级：手动设置的serviceClass > 自动解析泛型类型；
	 * 自动解析逻辑：通过{@link ResolvableType}解析当前类的泛型参数（ConfigurableServices<S>的S），
	 * 解析失败时返回null（需上层处理）。
	 *
	 * @return 目标服务类（可能为null，需通过setServiceClass手动设置）
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends S> getServiceClass() {
		// 双重检查锁定：避免每次调用都加锁，兼顾性能与线程安全
		if (serviceClass == null) {
			synchronized (this) {
				if (serviceClass == null) {
					try {
						// 解析当前类的泛型参数（ConfigurableServices<S>的第一个泛型参数）
						ResolvableType resolvableType = ResolvableType.forType(getClass())
								.as(ConfigurableServices.class);
						serviceClass = (Class<S>) resolvableType.getActualTypeArgument(0).getRawType();
					} catch (Exception e) {
						// 泛型解析失败时保留null，由configure方法抛出精准异常
						serviceClass = null;
					}
				}
			}
		}
		return serviceClass;
	}

	/**
	 * 手动设置目标服务类（覆盖自动解析的泛型类型）
	 *
	 * @param serviceClass 目标服务类（不可为null，由lombok @NonNull强制校验）
	 */
	public void setServiceClass(@NonNull Class<? extends S> serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * 获取配置操作结果（缓存）
	 * <p>
	 * 若未执行配置/配置失败，返回包含精准异常信息的失败Operation，永不返回null。
	 *
	 * @return 配置操作结果：
	 *         <ul>
	 *         <li>已配置成功：返回成功的Operation；</li>
	 *         <li>未配置/配置失败：返回包含异常信息的失败Operation。</li>
	 *         </ul>
	 */
	public Operation getConfigureOperation() {
		// 精准英文异常：未执行配置
		if (configureOperation == null) {
			return Operation.failure(new IllegalStateException(
					"Configuration operation has not been executed, please call the configure method first"));
		}
		return configureOperation;
	}

	/**
	 * 执行服务配置（核心方法）
	 * <p>
	 * 流程： 1. 双重检查锁定：仅首次调用/上一次配置失败时执行配置逻辑； 2. 获取目标服务类（自动解析/手动设置）； 3. 通过服务发现器获取服务实例流；
	 * 4. 批量注册服务实例，缓存配置结果。
	 *
	 * @param serviceDiscoverer 服务发现器（不可为null，用于获取服务实例）
	 * @return 配置操作结果（成功/失败）
	 * @throws NullPointerException 若serviceDiscoverer为null（由@NonNull强制校验）
	 */
	@Override
	public Operation configure(@NonNull ServiceDiscoverer serviceDiscoverer) {
		// 双重检查锁定：避免重复配置，仅未配置/配置失败时执行
		if (configureOperation == null || !configureOperation.isSuccess()) {
			synchronized (this) {
				if (configureOperation == null || !configureOperation.isSuccess()) {
					Class<? extends S> targetServiceClass = getServiceClass();
					// 精准英文异常：服务类解析失败
					if (targetServiceClass == null) {
						this.configureOperation = Operation.failure(new IllegalStateException(
								"Service class resolution failed: No generic type parameter of ConfigurableServices detected, and serviceClass was not set manually"));
					} else {
						try {
							// 通过服务发现器获取服务实例流
							Streamable<? extends S> serviceInstances = serviceDiscoverer.getServices(targetServiceClass);
							// 批量注册服务实例，缓存配置结果
							Operation registerOperation = registerAll(serviceInstances);
							// 兜底：防止registerAll返回null（补充空值防御）
							this.configureOperation = registerOperation == null
									? Operation.failure(new RuntimeException(
											"Batch registration of service instances returned a null result"))
									: registerOperation;
						} catch (Exception e) {
							// 精准英文异常：配置过程异常（包含根因）
							this.configureOperation = Operation.failure(new RuntimeException(
									"Service configuration failed: Exception occurred while retrieving/registering service instances",
									e));
						}
					}
				}
			}
		}
		return configureOperation;
	}
}