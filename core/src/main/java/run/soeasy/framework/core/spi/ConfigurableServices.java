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
 * <li>空值防御：全链路非空校验，避免空指针风险。</li>
 * </ul>
 *
 * @param <S> 服务接口/抽象类的泛型类型
 * @author soeasy.run
 * @see Services
 * @see Configurable
 * @see ServiceDiscoverer
 */
public class ConfigurableServices<S> extends Services<S> implements Configurable {
	/** JDK原生日志组件，用于记录配置容器的运行日志 */
	private static final Logger log = Logger.getLogger(ConfigurableServices.class.getName());

	/**
	 * 目标服务类（泛型{S}对应的实际类型）
	 * <p>
	 * 1. volatile修饰保证多线程下的可见性，避免DCL场景下的指令重排问题；
	 * 2. 支持两种设置方式：自动解析（基于{@link ResolvableType}解析泛型）、手动设置（{@link #setServiceClass(Class)}）；
	 * 3. 优先级：手动设置的值 &gt; 自动解析的泛型类型。
	 * </p>
	 */
	private volatile Class<? extends S> serviceClass;

	/**
	 * 构造可配置服务容器，指定服务实例的比较器。
	 * <p>
	 * 比较器决定服务实例的排序规则，影响服务注入时的优先级。
	 * </p>
	 *
	 * @param comparator 服务实例的比较器，不可为null
	 * @throws NullPointerException 若comparator为null时抛出
	 */
	public ConfigurableServices(@NonNull Comparator<? super S> comparator) {
		super(comparator);
	}

	/**
	 * 获取目标服务类（自动解析泛型+双重检查锁定保证线程安全）
	 * <p>
	 * 核心逻辑：
	 * <ol>
	 * <li>优先级：手动设置的serviceClass &gt; 自动解析的泛型类型；</li>
	 * <li>自动解析流程：
	 *   <ul>
	 *   <li>通过{@link ResolvableType#forType(java.lang.reflect.Type)}获取当前类的类型信息；</li>
	 *   <li>转换为{@link ConfigurableServices}类型，提取第0个泛型参数（即{S}）；</li>
	 *   <li>获取泛型参数对应的原始类型，转换为{@code Class&lt;? extends S&gt;}；</li>
	 *   </ul>
	 * </li>
	 * <li>异常处理：解析失败时记录WARNING日志，返回null。</li>
	 * </ol>
	 *
	 * @return 目标服务类，泛型解析失败时返回null
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
	 * 注意事项：设置后不会自动触发配置刷新，需手动调用{@link #configure(ServiceDiscoverer)}重新执行配置。
	 * </p>
	 *
	 * @param serviceClass 目标服务类，不可为null，需为泛型{S}的实现类/子类
	 * @throws NullPointerException 若serviceClass为null时抛出
	 */
	public void setServiceClass(@NonNull Class<? extends S> serviceClass) {
		this.serviceClass = serviceClass;
		log.fine("Manually set service class to: " + serviceClass.getName());
	}

	/**
	 * 配置结果缓存，键为{@link ServiceDiscoverer}，值为{@link Operation}
	 * <p>
	 * 1. 基于{@link ConcurrentHashMap}实现，保证多线程下的并发安全；
	 * 2. 每次调用{@link #configure(ServiceDiscoverer)}都会尝试更新该缓存；
	 * 3. 缓存值记录配置操作的结果，并支持回滚能力判断。
	 * </p>
	 */
	private final ConcurrentHashMap<ServiceDiscoverer, Operation> configurationMap = new ConcurrentHashMap<>();

	/**
	 * 执行/刷新服务配置（实现{@link Configurable#configure(ServiceDiscoverer)}接口）
	 * <p>
	 * 刷新核心规则：
	 * <ol>
	 * <li>前置校验：获取目标服务类，若为null则返回失败的{@link Operation}；</li>
	 * <li>旧配置校验：
	 *   <ul>
	 *   <li>旧配置存在 &amp;&amp; 不支持回滚 → 拒绝刷新，返回旧配置；</li>
	 *   <li>旧配置存在 &amp;&amp; 支持回滚 → 先执行回滚，再执行新配置；</li>
	 *   <li>无旧配置 → 直接执行首次配置；</li>
	 *   </ul>
	 * </li>
	 * <li>新配置执行：通过服务发现器获取服务实例，注册到容器并返回新的{@link Operation}；</li>
	 * <li>异常处理：配置过程中抛出异常时，记录SEVERE日志并返回失败的{@link Operation}。</li>
	 * </ol>
	 *
	 * @param serviceDiscoverer 服务发现器，不可为null，用于获取待注册的服务实例
	 * @return 配置操作结果：
	 *         <ul>
	 *         <li>成功：包含注册的服务实例数量、回滚能力等信息；</li>
	 *         <li>失败：包含异常信息、失败原因；</li>
	 *         <li>拒绝刷新：返回旧配置的{@link Operation}；</li>
	 *         </ul>
	 * @throws NullPointerException 若serviceDiscoverer为null时抛出
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