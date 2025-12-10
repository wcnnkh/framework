package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import lombok.NonNull;

/**
 * CompletableFuture到ListenableFuture的适配器，实现接口转换。
 * 将Java标准库的{@link CompletableFuture}适配为框架定义的{@link ListenableFuture}接口，
 * 使标准异步任务能够融入框架的事件监听体系，实现异步结果的统一处理。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>双向适配：支持从{@link CompletionStage}或{@link CompletableFuture}创建适配器</li>
 * <li>事件转发：通过{@link Stage}组件将CompletableFuture的完成状态转发至监听器</li>
 * <li>状态同步：保持CompletableFuture与ListenableFuture的状态一致性</li>
 * <li>非侵入式转换：不修改原CompletableFuture的逻辑，通过包装实现功能扩展</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>在框架中使用Java标准异步任务时保持接口一致性</li>
 * <li>为CompletableFuture添加框架的事件监听和状态查询能力</li>
 * <li>统一项目中不同异步编程模型的接口规范</li>
 * </ul>
 *
 * @param <T> 异步任务的结果类型
 * 
 * @author soeasy.run
 * @see ListenableFuture
 * @see CompletableFuture
 */
public class CompletableToListenableFutureAdapter<T> implements ListenableFuture<T> {

	/** 被适配的CompletableFuture实例，存储异步任务逻辑 */
	private final CompletableFuture<T> completableFuture;

	/** 状态管理组件，负责事件分发和状态维护 */
	private ListenableStage<T> stage = new ListenableStage<T>();

	/**
	 * 基于CompletionStage创建适配器（自动转换为CompletableFuture）
	 * <p>
	 * 该构造函数会将输入的CompletionStage转换为CompletableFuture，
	 * 并注册状态变更处理器以同步状态到ListenableFuture。
	 * 
	 * @param completionStage 待适配的CompletionStage实例
	 * @throws NullPointerException 若completionStage为null
	 */
	public CompletableToListenableFutureAdapter(@NonNull CompletionStage<T> completionStage) {
		this(completionStage.toCompletableFuture());
	}

	/**
	 * 基于CompletableFuture创建适配器
	 * <p>
	 * 该构造函数会注册CompletableFuture的完成处理器， 当CompletableFuture状态变更时，同步更新Stage的状态并触发事件。
	 * 
	 * @param completableFuture 待适配的CompletableFuture实例
	 * @throws NullPointerException 若completableFuture为null
	 */
	public CompletableToListenableFutureAdapter(@NonNull CompletableFuture<T> completableFuture) {
		this.completableFuture = completableFuture;
		this.completableFuture.handle(new BiFunction<T, Throwable, Object>() {
			@Override
			public Object apply(T result, Throwable ex) {
				if (ex != null) {
					// 解包CompletionException获取原始异常
					Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
					stage.tryFailure(cause);
				} else {
					stage.trySuccess(result);
				}
				return null;
			}
		});
	}

	/**
	 * 注册事件监听器，当异步任务状态变化时触发
	 * <p>
	 * 监听器会在任务完成（成功/失败/取消）时接收当前ListenableFuture实例， 支持通过返回的Operation取消监听。
	 * 
	 * @param listener 事件监听器，接收ListenableFuture&lt;T&gt;类型参数
	 * @return 注册回执，用于取消监听
	 * @throws NullPointerException 若listener为null
	 */
	@Override
	public Operation registerListener(@NonNull Listener<ListenableFuture<T>> listener) {
		return stage.registerListener((e) -> listener.accept(this));
	}

	/**
	 * 尝试取消异步任务
	 * <p>
	 * 该方法会将取消请求转发给CompletableFuture，并同步更新Stage状态。
	 * 
	 * @param mayInterruptIfRunning 是否中断运行中的任务
	 * @return 若取消成功返回true，否则返回false
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (this.completableFuture.cancel(mayInterruptIfRunning)) {
			stage.cancel();
			return true;
		}
		return false;
	}

	/**
	 * 检查任务是否已被取消
	 * 
	 * @return 若已取消返回true，否则返回false
	 */
	@Override
	public boolean isCancelled() {
		return this.completableFuture.isCancelled();
	}

	/**
	 * 检查任务是否已完成
	 * 
	 * @return 若已完成返回true，否则返回false
	 */
	@Override
	public boolean isDone() {
		return this.completableFuture.isDone();
	}

	/**
	 * 阻塞获取任务结果（直到完成）
	 * <p>
	 * 该方法直接转发至CompletableFuture的get()方法， 会阻塞当前线程直到任务完成或抛出异常。
	 * 
	 * @return 任务结果
	 * @throws InterruptedException 等待时线程被中断
	 * @throws ExecutionException   任务执行异常
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		return this.completableFuture.get();
	}

	/**
	 * 带超时控制的阻塞获取任务结果
	 * 
	 * @param timeout 最大等待时间
	 * @param unit    时间单位
	 * @return 任务结果
	 * @throws InterruptedException 等待时线程被中断
	 * @throws ExecutionException   任务执行异常
	 * @throws TimeoutException     等待超时
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.completableFuture.get(timeout, unit);
	}

	/**
	 * 检查任务是否可取消
	 * <p>
	 * 该方法通过Stage组件查询任务的可取消状态， 即任务是否处于未完成状态（可取消）。
	 * 
	 * @return 若可取消返回true，否则返回false
	 */
	@Override
	public boolean isCancellable() {
		return stage.isCancellable();
	}

	/**
	 * 获取任务失败的原因
	 * <p>
	 * 该方法通过Stage组件获取失败原因， 若任务成功或未失败则返回null。
	 * 
	 * @return 失败原因，若无异常返回null
	 */
	@Override
	public Throwable cause() {
		return stage.cause();
	}

	/**
	 * 检查任务是否成功完成
	 * 
	 * @return 若成功完成返回true，否则返回false
	 */
	@Override
	public boolean isSuccess() {
		return stage.isSuccess();
	}

	/**
	 * 非阻塞获取当前结果（若可用）
	 * <p>
	 * 此方法通过Stage组件获取结果，需确保Stage实现的类型安全性。
	 * 
	 * @return 已完成的结果，若未完成返回null
	 */
	@Override
	public T getNow() {
		return stage.getNow();
	}
}