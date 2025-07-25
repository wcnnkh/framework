package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.transmittable.Inheriter;

public class InheritableScheduledExecutorService<A, B, I extends Inheriter<A, B>, W extends ScheduledExecutorService>
		extends InheritableExecutorService<A, B, I, W> implements ScheduledExecutorService {

	/**
	 * 创建支持上下文传递的ScheduledExecutorService包装器。
	 * 
	 * @param source    被包装的原始ScheduledExecutorService实例，不可为null
	 * @param inheriter 用于管理上下文的继承器，不可为null
	 * @throws NullPointerException 如果source或inheriter为null
	 */
	public InheritableScheduledExecutorService(W source, I inheriter) {
		super(source, inheriter);
	}

	/**
	 * 提交一个延迟执行的Callable任务，返回一个表示任务的未决结果的ScheduledFuture。
	 * 任务会被包装为{@link WrappedCallable}以确保上下文传递。
	 * 
	 * @param callable  要执行的任务
	 * @param delay     从现在开始延迟执行的时间
	 * @param unit      延迟参数的时间单位
	 * @param <V> 任务返回类型
	 * @return 表示任务的ScheduledFuture，该Future的{@code get}方法在成功完成时将返回任务的结果
	 */
	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return getSource().schedule(new WrappedCallable<>(callable, getInheriter()), delay, unit);
	}

	/**
	 * 提交一个延迟执行的Runnable任务，返回一个表示该任务的ScheduledFuture。
	 * 任务会被包装为{@link WrappedRunnable}以确保上下文传递。
	 * 
	 * @param command 要执行的任务
	 * @param delay   从现在开始延迟执行的时间
	 * @param unit    延迟参数的时间单位
	 * @return 表示任务的ScheduledFuture，该Future的{@code get}方法在成功完成时将返回{@code null}
	 */
	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return getSource().schedule(new WrappedRunnable<>(command, getInheriter()), delay, unit);
	}

	/**
	 * 提交一个固定速率执行的Runnable任务，返回一个表示任务的ScheduledFuture。
	 * 任务会被包装为{@link WrappedRunnable}以确保上下文传递。
	 * 
	 * @param command      要执行的任务
	 * @param initialDelay 首次执行的延迟时间
	 * @param period       连续执行之间的周期
	 * @param unit         参数的时间单位
	 * @return 表示任务的ScheduledFuture，该Future可用于取消任务或检查任务状态
	 */
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return getSource().scheduleAtFixedRate(new WrappedRunnable<>(command, getInheriter()), initialDelay, period,
				unit);
	}

	/**
	 * 提交一个固定延迟执行的Runnable任务，返回一个表示任务的ScheduledFuture。
	 * 任务会被包装为{@link WrappedRunnable}以确保上下文传递。
	 * 
	 * @param command      要执行的任务
	 * @param initialDelay 首次执行的延迟时间
	 * @param delay        一次执行终止和下一次执行开始之间的延迟
	 * @param unit         参数的时间单位
	 * @return 表示任务的ScheduledFuture，该Future可用于取消任务或检查任务状态
	 */
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return getSource().scheduleWithFixedDelay(new WrappedRunnable<>(command, getInheriter()), initialDelay, delay,
				unit);
	}
}