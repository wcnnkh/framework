package run.soeasy.framework.io.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;

import javax.lang.model.util.Elements;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.concurrent.Poller;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * WatchKey事件轮询器，继承自{@link Poller}，用于通过轮询方式处理{@link WatchKey}中的文件系统事件（{@link WatchEvent}），
 * 支持按事件上下文类型过滤事件，并通过{@link Publisher}发布符合条件的事件，是Java NIO文件系统监控（WatchService）的配套组件。
 * 
 * <p>核心功能：
 * - 从{@link WatchKey}中提取事件，过滤出上下文类型匹配的{@link WatchEvent}；
 * - 定期轮询事件并发布，配合{@link Poller}的调度能力（如定时执行）实现持续监控；
 * - 自动重置{@link WatchKey}以确保其能继续接收新事件，避免监控中断。
 * 
 * @param <T> 事件上下文的类型（如{@link java.nio.file.Path}，表示文件系统路径）
 * @author soeasy.run
 * @see Poller
 * @see WatchKey
 * @see WatchEvent
 * @see Publisher
 */
@RequiredArgsConstructor
@Getter
public class WatchKeyPoller<T> extends Poller {

    /**
     * 从指定的{@link WatchKey}中提取并过滤出上下文类型匹配的{@link WatchEvent}集合
     * 
     * <p>处理逻辑：
     * 1. 检查{@link WatchKey}是否有效（{@link WatchKey#isValid()}），无效则返回空集合；
     * 2. 通过{@link WatchKey#pollEvents()}获取所有事件；
     * 3. 过滤出上下文对象类型匹配{@code contextType}的事件，并转换为泛型{@link WatchEvent}；
     * 4. 用{@link Elements}包装过滤后的事件集合（支持集合操作）。
     * 
     * @param watchKey 待提取事件的WatchKey（非空，关联到特定目录的监控）
     * @param contextType 事件上下文的目标类型（非空，如Path.class，用于过滤事件）
     * @param <T> 事件上下文类型
     * @return 符合条件的事件集合（非空，可能为空集合）
     */
    @SuppressWarnings("unchecked")
    public static <T> Streamable<WatchEvent<T>> pollEvents(WatchKey watchKey, Class<T> contextType) {
        if (!watchKey.isValid()) {
            return Streamable.empty();
        }

        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
        if (CollectionUtils.isEmpty(watchEvents)) {
            return Streamable.empty();
        }

        // 过滤上下文类型匹配的事件，并转换为泛型类型
        return Streamable.of(watchEvents)
                .filter(event -> contextType.isInstance(event.context()))
                .map(event -> (WatchEvent<T>) event);
    }

    /**
     * 被监控的WatchKey实例（非空），关联到文件系统中的特定监控目录，轮询器通过它获取文件系统事件
     */
    @NonNull
    private final WatchKey watchKey;

    /**
     * 事件上下文的目标类型（非空），用于过滤WatchEvent，仅保留上下文对象类型匹配的事件（如Path类型的文件路径事件）
     */
    @NonNull
    private final Class<T> contextType;

    /**
     * 事件发布者（非空），用于将过滤后的{@link WatchEvent}集合发布给订阅者，
     * 实现事件的分发与处理解耦（如后续可扩展为日志记录、业务处理等）。
     */
    @NonNull
    private final Publisher<? super Streamable<WatchEvent<T>>> watchEventProducer;

    /**
     * 从当前WatchKey中提取并过滤符合上下文类型的事件，调用静态{@link #pollEvents(WatchKey, Class)}实现
     * 
     * @return 符合条件的事件集合（非空，可能为空集合）
     */
    public Streamable<WatchEvent<T>> pollEvents() {
        return pollEvents(watchKey, contextType);
    }

    /**
     * 重置当前WatchKey，使其能继续接收新的文件系统事件
     * 
     * <p>WatchKey在调用{@link WatchKey#pollEvents()}后需要重置才能再次生效，否则无法接收后续事件，
     * 此方法封装{@link WatchKey#reset()}操作，确保监控持续有效。
     */
    public void reset() {
        watchKey.reset();
    }

    /**
     * 轮询执行的核心方法：提取事件、发布事件、重置WatchKey
     * 
     * <p>执行流程：
     * 1. 调用{@link #pollEvents()}从WatchKey中获取过滤后的事件集合；
     * 2. 通过{@link #watchEventProducer}发布事件集合（即使为空集合，也允许下游处理“无事件”场景）；
     * 3. 无论发布是否成功，在finally块中调用{@link #reset()}重置WatchKey，确保后续能继续接收事件。
     */
    @Override
    public void run() {
    	Streamable<WatchEvent<T>> events = pollEvents();
        try {
            watchEventProducer.publish(events);
        } finally {
            // 必须重置WatchKey，否则无法接收后续事件
            reset();
        }
    }
}