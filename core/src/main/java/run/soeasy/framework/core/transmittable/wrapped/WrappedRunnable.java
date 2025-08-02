package run.soeasy.framework.core.transmittable.wrapped;

import lombok.NonNull;
import run.soeasy.framework.core.transmittable.Inheriter;

/**
 * 支持上下文传递的可运行任务包装器。 该类继承自{@link Captured}，用于包装{@link Runnable}任务，
 * 在任务执行前后自动管理上下文状态的捕获、重放和恢复， 确保上下文在异步执行环境中正确传递。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>上下文透明传递：在异步任务执行时自动携带上下文状态</li>
 * <li>状态隔离保证：每个任务执行前后自动恢复上下文，避免相互干扰</li>
 * <li>异常安全：通过try-finally确保上下文恢复，即使任务执行抛出异常</li>
 * <li>无缝集成：与线程池、定时任务等现有执行框架完全兼容</li>
 * </ul>
 *
 * <p>
 * 典型使用场景：
 * 
 * <pre>
 * WrappedRunnable&lt;Context, Backup, MyInheriter, Exception, Runnable&gt; wrappedTask = new WrappedRunnable&lt;&gt;(
 * 		() -&gt; System.out.println("Task execution"), contextInheriter);
 * 
 * executorService.execute(wrappedTask);
 * </pre>
 *
 * <p>
 * 设计考量：
 * <ul>
 * <li>零侵入性：不修改原始Runnable接口，保持使用方式一致性</li>
 * <li>线程安全：上下文状态的捕获和恢复操作具有原子性</li>
 * <li>性能优化：通过预捕获上下文减少重复操作</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <E> 任务执行可能抛出的异常类型
 * @param <W> 被包装的原始Runnable类型
 * 
 * @see Captured
 * @see Runnable
 * @see Inheriter
 */
public class WrappedRunnable<A, B, I extends Inheriter<A, B>, E extends Throwable, W extends Runnable>
		extends Captured<A, B, I, W> implements Runnable {

	/**
	 * 创建支持上下文传递的Runnable任务包装器。
	 * 
	 * @param source    被包装的原始Runnable任务，不可为null
	 * @param inheriter 用于管理上下文的继承器，不可为null
	 */
	public WrappedRunnable(@NonNull W source, @NonNull I inheriter) {
		super(source, inheriter);
	}

	/**
	 * 执行被包装的Runnable任务，并在执行前后自动管理上下文状态。 具体执行流程：
	 * <ol>
	 * <li>使用预捕获的上下文状态重放当前环境</li>
	 * <li>执行原始Runnable任务</li>
	 * <li>无论任务是否抛出异常，始终恢复原始上下文状态</li>
	 * </ol>
	 */
	@Override
	public void run() {
		B backup = getInheriter().replay(getCapture());
		try {
			this.getSource().run();
		} finally {
			getInheriter().restore(backup);
		}
	}
}