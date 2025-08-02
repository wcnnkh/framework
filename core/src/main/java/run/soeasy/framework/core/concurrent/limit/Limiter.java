package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.locks.Lock;

/**
 * 资源限制器接口，用于控制对资源的访问权限和并发限制。 该接口定义了三种核心操作：检查限制状态、设置限制状态和获取资源锁。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>状态管理：通过{@link #isLimited()}和{@link #limited()}方法管理限制状态</li>
 * <li>资源获取：通过{@link #getResource()}方法获取访问资源的锁</li>
 * <li>灵活实现：支持多种限制策略（如限流、熔断、资源池等）</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>流量控制：限制系统处理的请求数量</li>
 * <li>资源保护：防止资源被过度使用</li>
 * <li>熔断机制：当系统异常时临时拒绝请求</li>
 * <li>资源池管理：控制资源池中的资源分配</li>
 * </ul>
 *
 * @author soeasy.run
 */
public interface Limiter {
	/**
	 * 检查当前是否处于受限状态。 受限状态下，通常应拒绝新的资源请求或采取降级策略。
	 *
	 * @return 若处于受限状态返回true，否则返回false
	 */
	boolean isLimited();

	/**
	 * 设置限制器为受限状态。 调用此方法后，{@link #isLimited()}将始终返回true，直到限制器状态被重置。
	 *
	 * @return 设置成功返回true，失败返回false（如已处于受限状态）
	 */
	boolean limited();

	/**
	 * 获取访问资源的锁。 调用者应在访问受限制资源前先获取锁，并在使用后释放锁。
	 *
	 * @return 用于控制资源访问的锁对象
	 */
	Lock getResource();
}