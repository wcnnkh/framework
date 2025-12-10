package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 可监听未来结果包装器接口，定义对ListenableFuture的包装器模式实现。
 * 该接口继承自{@link ListenableFuture}和{@link ListenableReceiptWrapper}，
 * 允许在不修改原有未来结果实例的情况下增强其功能，如添加日志记录、结果转换或状态监控等。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>透明代理：默认实现将所有操作委派给源未来结果实例</li>
 * <li>功能增强：子类可选择性覆盖方法以添加额外行为</li>
 * <li>类型安全：通过泛型参数确保包装对象类型一致性</li>
 * </ul>
 *
 * <p>
 * 典型应用场景：
 * <ul>
 * <li>添加异步操作的日志记录功能</li>
 * <li>实现异步结果的转换或增强</li>
 * <li>添加异步操作的超时监控</li>
 * <li>实现异步结果的缓存策略</li>
 * </ul>
 *
 * @param <V> 异步操作返回的结果类型
 * @param <W> 包装器自身的类型，需实现当前接口
 * 
 * @author soeasy.run
 * @see ListenableFuture
 * @see ListenableReceiptWrapper
 */
@FunctionalInterface
public interface ListenableFutureWrapper<V, W extends ListenableFuture<V>>
		extends ListenableFuture<V>, ListenableOperationWrapper<ListenableFuture<V>, W> {

	/**
	 * 立即获取源未来结果的当前结果 默认实现将操作委派给源未来结果实例
	 * 
	 * @return 当前结果，如果操作未完成或失败则返回null
	 */
	@Override
	default V getNow() {
		return getSource().getNow();
	}

	/**
	 * 尝试取消源未来结果的异步操作 默认实现将操作委派给源未来结果实例
	 * 
	 * @return 如果操作成功取消返回true，否则返回false
	 */
	@Override
	default boolean cancel() {
		return getSource().cancel();
	}

	/**
	 * 等待源未来结果的异步操作完成 默认实现将操作委派给源未来结果实例
	 * 
	 * @param timeout 最大等待时间
	 * @param unit    时间单位
	 * @return 如果操作在超时前完成返回true，否则返回false
	 * @throws InterruptedException 如果当前线程在等待时被中断
	 */
	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return getSource().await(timeout, unit);
	}

	/**
	 * 检查源未来结果的异步操作是否已完成 默认实现将操作委派给源未来结果实例
	 * 
	 * @return 如果操作已完成返回true，否则返回false
	 */
	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	/**
	 * 检查源未来结果的异步操作是否已被取消 默认实现将操作委派给源未来结果实例
	 * 
	 * @return 如果操作已被取消返回true，否则返回false
	 */
	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}

	/**
	 * 尝试取消异步操作（可指定是否中断运行中的任务）
	 * 
	 * @param mayInterruptIfRunning 若为true则中断运行中的任务，否则仅标记为取消
	 * @return 如果操作成功取消返回true，否则返回false
	 */
	@Override
	default boolean cancel(boolean mayInterruptIfRunning) {
		return getSource().cancel(mayInterruptIfRunning);
	}

	/**
	 * 阻塞获取异步操作结果（直到操作完成）
	 * 
	 * @return 异步操作的结果
	 * @throws InterruptedException 如果当前线程在等待时被中断
	 * @throws ExecutionException   如果操作执行过程中发生异常
	 */
	@Override
	default V get() throws InterruptedException, ExecutionException {
		return getSource().get();
	}

	/**
	 * 带超时控制的阻塞获取异步操作结果
	 * 
	 * @param timeout 最大等待时间
	 * @param unit    时间单位
	 * @return 异步操作的结果
	 * @throws InterruptedException 如果当前线程在等待时被中断
	 * @throws ExecutionException   如果操作执行过程中发生异常
	 * @throws TimeoutException     如果等待时间超过timeout
	 */
	@Override
	default V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return getSource().get(timeout, unit);
	}

	@Override
	default boolean isRollback() {
		return getSource().isRollback();
	}

	@Override
	default boolean rollback() {
		return getSource().rollback();
	}

	@Override
	default boolean isRollbackSupported() {
		return getSource().isRollbackSupported();
	}

	@Override
	default void await() throws InterruptedException {
		getSource().await();
	}

}