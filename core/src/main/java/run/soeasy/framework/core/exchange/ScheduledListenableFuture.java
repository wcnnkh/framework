package run.soeasy.framework.core.exchange;

import java.util.concurrent.ScheduledFuture;

/**
 * 定时可监听未来结果接口，整合定时任务调度和可监听异步结果的功能。
 * 该接口继承自{@link ScheduledFuture}和{@link ListenableFuture}，
 * 允许对定时执行的异步操作结果进行监听，同时支持定时任务的调度特性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>定时调度：继承ScheduledFuture的定时执行和延迟获取功能</li>
 *   <li>事件监听：继承ListenableFuture的完成/成功/失败事件监听机制</li>
 *   <li>结果获取：支持阻塞获取、非阻塞获取和超时获取结果</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>需要结果监听的定时任务调度</li>
 *   <li>延迟执行的异步操作结果追踪</li>
 *   <li>周期性任务的执行状态监控</li>
 *   <li>带超时控制的定时异步操作</li>
 * </ul>
 *
 * @param <V> 异步操作返回的结果类型
 * 
 * @author soeasy.run
 * @see ScheduledFuture
 * @see ListenableFuture
 */
public interface ScheduledListenableFuture<V> extends ScheduledFuture<V>, ListenableFuture<V> {
    // 继承自ScheduledFuture和ListenableFuture，无额外方法
}