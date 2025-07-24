package run.soeasy.framework.io.watch;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.concurrent.Poller;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;
import run.soeasy.framework.io.Resource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 资源轮询监控器，继承自{@link Poller}，通过定期检测资源的最后修改时间追踪资源状态变化（创建、更新、删除），
 * 并将变更事件通过{@link Publisher}发布，是监控{@link Resource}类型资源状态的通用组件。
 * 
 * <p>核心机制：
 * - 基于时间戳对比：通过资源的{@link Resource#lastModified()}方法获取最后修改时间，与上一次记录的时间戳对比；
 * - 事件驱动：根据时间戳变化判断变更类型（创建/更新/删除），封装为{@link ChangeEvent}并发布；
 * - 线程安全：关键操作（如时间戳获取与对比）通过同步块保证线程安全，避免并发场景下的状态不一致。
 * 
 * @param <T> 资源类型，需实现{@link Resource}接口（如文件资源、网络资源等可获取最后修改时间的资源）
 * @author soeasy.run
 * @see Poller
 * @see Resource
 * @see ChangeEvent
 * @see Publisher
 */
@RequiredArgsConstructor
@Getter
public class ResourcePoller<T extends Resource> extends Poller {
    private static final Logger logger = LogManager.getLogger(ResourcePoller.class);

    /**
     * 被监控的资源实例（非空），轮询器通过该实例获取最后修改时间以检测状态变化
     */
    @NonNull
    private final T resource;

    /**
     * 变更事件发布者（非空），用于将资源变更事件发布给订阅者，
     * 支持事件的分发与处理解耦（如后续可扩展为异步通知、多订阅者等）。
     */
    @NonNull
    private final Publisher<? super Elements<ChangeEvent<T>>> changeEventProducer;

    /**
     * 上一次记录的资源最后修改时间（毫秒时间戳），初始值为0，
     * 作为判断资源是否变更的基准（0通常表示资源不存在或未初始化）。
     */
    private volatile long lastModified = 0L;

    /**
     * 轮询执行的核心方法，定期检查资源当前状态并触发变更事件
     * 
     * <p>执行流程：
     * 1. 同步块内操作，确保时间戳获取与状态更新的原子性；
     * 2. 调用{@link Resource#lastModified()}获取当前资源的最后修改时间，若抛出{@link IOException}（如资源暂时不可访问），
     *    仅通过warn级别日志记录，不中断轮询；
     * 3. 调用{@link #touchEvent(long, long)}对比当前与上一次的时间戳，判断是否发布变更事件；
     * 4. 更新{@link #lastModified}为当前时间戳，作为下一次轮询的基准。
     */
    @Override
    public void run() {
        synchronized (this) {
            long currentLastModified = 0L;
            try {
                // 获取资源当前的最后修改时间（Resource接口定义的标准方法）
                currentLastModified = resource.lastModified();
            } catch (IOException e) {
                // 忽略临时异常（如资源被占用、网络波动），仅记录跟踪日志
                logger.warn(e, "Failed to get lastModified for resource [{}], ignoring", resource.getDescription());
            }
            // 对比时间戳并触发事件
            touchEvent(currentLastModified, this.lastModified);
            // 更新上一次的时间戳
            this.lastModified = currentLastModified;
        }
    }

    /**
     * 根据当前与历史时间戳判断资源变更类型，并发布对应事件
     * 
     * <p>变更类型判断规则：
     * - 若当前时间戳为0且历史时间戳不为0 → 资源被删除（{@link ChangeType#DELETE}）；
     * - 若历史时间戳为0且当前时间戳不为0 → 资源被创建（{@link ChangeType#CREATE}）；
     * - 若两者均不为0且不相等 → 资源内容被更新（{@link ChangeType#UPDATE}）；
     * - 若两者相等 → 无变更，不发布事件。
     * 
     * @param current 当前资源的最后修改时间（毫秒时间戳）
     * @param previous 上一次记录的最后修改时间（毫秒时间戳）
     */
    private void touchEvent(long current, long previous) {
        if (current == previous) {
            // 时间戳相同，资源无变更，直接返回
            return;
        }

        // 根据时间戳变化创建对应类型的变更事件
        ChangeEvent<T> changeEvent;
        if (current == 0) {
            // 当前时间戳为0 → 资源已删除
            changeEvent = new ChangeEvent<>(resource, ChangeType.DELETE);
        } else if (previous == 0) {
            // 历史时间戳为0，当前不为0 → 资源新创建
            changeEvent = new ChangeEvent<>(resource, ChangeType.CREATE);
        } else {
            // 时间戳变化且均不为0 → 资源已更新
            changeEvent = new ChangeEvent<>(resource, ChangeType.UPDATE);
        }

        // 将事件包装为单元素集合并发布（Elements用于支持批量事件，此处兼容单事件场景）
        changeEventProducer.publish(Elements.singleton(changeEvent));
    }
}