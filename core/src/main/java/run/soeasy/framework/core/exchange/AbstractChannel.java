package run.soeasy.framework.core.exchange;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.exchange.future.ListenableFutureTask;

/**
 * 通道抽象基类，提供消息通道的基本实现框架。
 * 该类实现了{@link Channel}接口，支持同步和异步消息发布模式，
 * 通过Executor的有无来决定采用同步还是异步方式处理消息。
 *
 * <p>核心特性：
 * <ul>
 *   <li>双模式支持：同步和异步消息发布</li>
 *   <li>可配置执行器：通过setPublishExecutor方法配置异步执行器</li>
 *   <li>结果跟踪：异步模式下返回ListenableFutureTask跟踪执行结果</li>
 * </ul>
 *
 * <p>实现注意事项：
 * <ul>
 *   <li>子类必须实现syncPublish方法定义同步发布逻辑</li>
 *   <li>timeout参数在当前实现中被忽略，仅用于接口兼容</li>
 *   <li>异步模式下的超时控制需由Executor实现提供</li>
 * </ul>
 *
 * @param <T> 通道中传递的消息类型
 * 
 * @author soeasy.run
 * @see Channel
 * @see ListenableFutureTask
 */
public abstract class AbstractChannel<T> implements Channel<T> {

    /**
     * 消息发布执行器，为null时使用同步模式
     */
    private Executor publishExecutor;

    /**
     * 获取消息发布执行器
     * 
     * @return 当前配置的执行器，可能为null
     */
    public Executor getPublishExecutor() {
        return publishExecutor;
    }

    /**
     * 设置消息发布执行器
     * 设置后，消息将通过该执行器异步发布
     * 
     * @param publishExecutor 用于异步发布消息的执行器，null表示使用同步模式
     */
    public void setPublishExecutor(Executor publishExecutor) {
        this.publishExecutor = publishExecutor;
    }

    /**
     * 发布消息到通道，并指定超时时间
     * <p>
     * 实现逻辑：
     * <ul>
     *   <li>若未设置执行器，直接调用syncPublish同步处理消息</li>
     *   <li>若设置了执行器，将消息处理任务提交到执行器异步执行</li>
     * </ul>
     * 
     * <p>注意：
     * <ul>
     *   <li>当前实现忽略timeout参数，所有异步任务均立即提交</li>
     *   <li>超时控制需由外部Executor实现提供</li>
     * </ul>
     * 
     * @param resource 待发布的消息资源
     * @param timeout 超时时间，当前实现忽略此参数
     * @param timeUnit 超时时间单位
     * @return 发布操作的回执，同步模式返回SUCCESS，异步模式返回ListenableFutureTask
     */
    @Override
    public Receipt publish(T resource, long timeout, TimeUnit timeUnit) {
        if (publishExecutor == null) {
            syncPublish(resource);
            return Receipt.SUCCESS;
        } else {
            ListenableFutureTask<?> task = new ListenableFutureTask<>(() -> syncPublish(resource), null);
            publishExecutor.execute(task);
            return task;
        }
    }

    /**
     * 同步发布消息的抽象方法
     * 子类必须实现此方法定义具体的消息发布逻辑
     * 
     * @param resource 待发布的消息资源
     */
    public abstract void syncPublish(T resource);
}