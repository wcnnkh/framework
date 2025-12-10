package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 可配置的服务提供者工厂，管理多个服务提供者工厂实例，实现服务发现的链式查找。
 * <p>
 * 该类继承自{@link Services}，将多个{@link ServiceDiscoverer}组合为一个复合工厂，
 * 实现服务提供者的链式查找，适用于需要整合多种服务发现机制的场景。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>链式查找：按注册顺序依次尝试每个工厂，直到找到匹配的服务提供者</li>
 * <li>动态配置：支持运行时添加、移除服务提供者工厂</li>
 * <li>空值安全：若所有工厂均未找到服务提供者，返回null</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Services
 * @see ServiceDiscoverer
 */
public class CompositeServiceDiscoverer extends Services<ServiceDiscoverer> implements ServiceDiscoverer {

	public CompositeServiceDiscoverer() {
		super(ServiceComparator.defaultServiceComparator());
	}

	@Override
	public <S> Streamable<S> getServices(@NonNull Class<S> requiredType) {
		return map((e) -> e.getServices(requiredType)).filter((e) -> e != null).first();
	}
}