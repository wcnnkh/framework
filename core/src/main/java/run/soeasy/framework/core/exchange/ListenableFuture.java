package run.soeasy.framework.core.exchange;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 可监听的异步未来结果接口，扩展{@link Future}并实现{@link ListenableOperation}，支持异步操作的事件监听与非阻塞结果获取。
 * <p>核心特性：
 * <ul>
 * <li>事件监听：支持注册完成、成功、失败事件监听器；</li>
 * <li>非阻塞获取：通过getNow()立即获取当前结果；</li>
 * <li>超时等待：提供带超时参数的await()方法；</li>
 * <li>兼容标准：完全实现Java标准{@link Future}接口。</li>
 * </ul>
 * <p>状态转换模型：
 * <ul>
 * <li>初始状态：未完成（pending）；</li>
 * <li>完成状态：操作正常完成，结果可用；</li>
 * <li>失败状态：操作因异常终止；</li>
 * <li>取消状态：操作被主动取消。</li>
 * </ul>
 *
 * @param <V> 异步操作返回的结果类型
 * @author soeasy.run
 * @see Future
 * @see ListenableOperation
 */
public interface ListenableFuture<V> extends ListenableOperation<ListenableFuture<V>>, Future<V> {

	/**
	 * 尝试取消异步操作。
	 * <p>默认实现调用cancel(false)，不中断正在执行的任务。
	 *
	 * @return 操作成功取消返回true，否则返回false
	 */
	@Override
	default boolean cancel() {
		return cancel(false);
	}

	/**
	 * 立即获取当前结果（非阻塞）。
	 * <p>返回规则：
	 * <ul>
	 * <li>操作成功完成：返回实际结果；</li>
	 * <li>操作未完成/失败：返回null。</li>
	 * </ul>
	 * <p>注意：成功结果可能为null，需结合isSuccess()判断；该方法不抛出异常。
	 *
	 * @return 当前结果，操作未完成/失败时返回null
	 */
	V getNow();

	/**
	 * 等待异步操作完成，阻塞当前线程直至操作完成/被取消/线程中断。
	 *
	 * @throws InterruptedException 当前线程在等待时被中断
	 */
	@Override
	default void await() throws InterruptedException {
		try {
			get();
		} catch (InterruptedException | ExecutionException e) {
		}
	}

	/**
	 * 等待异步操作完成，最多等待指定时间。
	 * <p>阻塞当前线程直至操作完成/被取消/超时/线程中断，超时返回false，异常时根据类型返回对应结果。
	 *
	 * @param timeout 超时时间
	 * @param unit    时间单位
	 * @return 超时返回false，操作完成（含失败）返回true
	 * @throws InterruptedException 当前线程在等待时被中断
	 */
	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			get(timeout, unit);
		} catch (TimeoutException e) {
			return false;
		} catch (ExecutionException e) {
			return true;
		}
		return true;
	}

	@Override
	default boolean rollback() {
		return false;
	}

	@Override
	default boolean isRollback() {
		return false;
	}

	@Override
	default boolean isRollbackSupported() {
		return false;
	}
}