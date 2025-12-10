package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * <li>线程安全配置：基于双重检查锁定（DCL）保证配置操作的线程安全；</li>
 * <li>配置刷新规则：调用configure方法即尝试刷新配置，但仅当旧配置支持回滚时才允许刷新；
 * <ul>
 * <li>旧配置存在且不支持回滚 → 拒绝刷新，返回旧配置结果；</li>
 * <li>旧配置存在且支持回滚 → 先回滚旧配置，再执行新配置；</li>
 * <li>无旧配置 → 直接执行首次配置；</li>
 * </ul>
 * </li>
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
	/** JDK原生日志（日志内容为英文，便于调试） */
	private static final Logger log = Logger.getLogger(ConfigurableServices.class.getName());

	/**
	 * 目标服务类（泛型对应的实际类型）
	 * <p>
	 * volatile保证多线程下的可见性，支持自动解析（泛型）和手动设置两种方式
	 * </p>
	 */
	private volatile Class<? extends S> serviceClass;

	public ConfigurableServices(@NonNull Comparator<? super S> comparator) {
		super(comparator);
	}

	/**
	 * 获取目标服务类（自动解析泛型+双重检查锁定）
	 * <p>
	 * 优先级：手动设置的serviceClass > 自动解析泛型类型；
	 * 自动解析逻辑：通过{@link ResolvableType}解析当前类的泛型参数（ConfigurableServices<S>的S），
	 * 解析失败时返回null（需通过setServiceClass手动设置）。
	 *
	 * @return 目标服务类（可能为null，需通过setServiceClass手动设置）
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends S> getServiceClass() {
		if (serviceClass == null) {
			synchronized (this) {
				if (serviceClass == null) {
					try {
						ResolvableType resolvableType = ResolvableType.forType(getClass())
								.as(ConfigurableServices.class);
						serviceClass = (Class<S>) resolvableType.getActualTypeArgument(0).getRawType();
						log.finer("Generic type resolved successfully, auto-identified service class: "
								+ serviceClass.getName());
					} catch (Exception e) {
						log.log(Level.WARNING,
								"Failed to resolve generic type, please set service class manually via setServiceClass",
								e);
						serviceClass = null;
					}
				}
			}
		}
		return serviceClass;
	}

	/**
	 * 手动设置目标服务类（覆盖自动解析的泛型类型）
	 * <p>
	 * 设置后不会自动刷新配置，需手动调用configure方法重新执行配置
	 * </p>
	 *
	 * @param serviceClass 目标服务类（不可为null，由lombok @NonNull强制校验）
	 */
	public void setServiceClass(@NonNull Class<? extends S> serviceClass) {
		this.serviceClass = serviceClass;
		log.fine("Manually set service class to: " + serviceClass.getName());
	}

	/** 配置结果缓存（每次调用configure都会尝试更新为最新结果） */
	private final ConcurrentHashMap<ServiceDiscoverer, Operation> configurationMap = new ConcurrentHashMap<>();

	/**
	 * 执行/刷新配置（每次调用都会触发配置逻辑，但需满足刷新规则）
	 * <p>
	 * 刷新核心规则： 1. 优先校验旧配置的回滚能力，不支持回滚则直接返回旧配置，拒绝刷新； 2. 支持回滚则先执行旧配置的回滚操作，再执行新配置逻辑； 3.
	 * 无旧配置时直接执行首次配置，无回滚步骤。
	 * </p>
	 *
	 * @param serviceDiscoverer 服务发现器（非null）
	 * @return 最新配置结果（若拒绝刷新则返回旧配置结果）
	 */
	@Override
	public Operation configure(@NonNull ServiceDiscoverer serviceDiscoverer) {
		Class<? extends S> serviceClass = getServiceClass();
		if (serviceClass == null) {
			String errorMsg = "Failed to get service class (generic type resolution failed and not set manually)";
			log.warning(errorMsg);
			return Operation.failure(new IllegalStateException(errorMsg));
		}

		return configurationMap.compute(serviceDiscoverer, (dis, oldOp) -> {
			// 步骤1：刷新前校验旧配置的回滚能力
			if (oldOp != null) {
				if (!oldOp.isRollbackSupported()) {
					String warnMsg = "Refresh rejected: old operation for service discoverer ["
							+ dis.getClass().getName() + "] does not support rollback";
					log.warning(warnMsg);
					return oldOp; // 拒绝刷新，返回旧配置
				}

				// 步骤2：执行旧配置的回滚操作
				boolean rollbackSuccess = oldOp.rollback();
				if (rollbackSuccess) {
					log.fine("Old operation for service discoverer [" + dis.getClass().getName()
							+ "] rolled back successfully before refresh");
				} else {
					log.warning("Failed to rollback old operation for service discoverer [" + dis.getClass().getName()
							+ "], proceed to refresh anyway");
				}
			}

			// 步骤3：执行新配置逻辑
			try {
				log.finer("Start refreshing configuration for service discoverer [" + dis.getClass().getName()
						+ "], service class: " + serviceClass.getName());
				Streamable<? extends S> serviceInstances = dis.getServices(serviceClass);
				Operation newOp = registerAll(serviceInstances);

				// 日志区分首次配置和刷新
				if (oldOp == null) {
					log.fine("First configuration for service discoverer [" + dis.getClass().getName()
							+ "] succeeded, number of registered service instances: " + serviceInstances.count());
				} else {
					log.fine("Configuration refreshed for service discoverer [" + dis.getClass().getName()
							+ "], number of registered service instances: " + serviceInstances.count());
				}
				return newOp;
			} catch (Exception e) {
				String errorMsg = "Exception thrown during configuration refresh for service discoverer ["
						+ dis.getClass().getName() + "], service class: " + serviceClass.getName();
				log.log(Level.SEVERE, errorMsg, e);
				return Operation.failure(e);
			}
		});
	}
}