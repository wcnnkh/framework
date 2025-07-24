package run.soeasy.framework.core.exchange.event;

import java.util.EventObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.time.Millisecond;
import run.soeasy.framework.sequences.UUIDSequence;

/**
 * 基础事件抽象类，定义所有事件的公共属性和行为规范。
 * <p>
 * 该类继承自Java标准库的{@link EventObject}，提供事件唯一标识、时间戳等基础属性，
 * 并支持事件链式传递和上下文继承，是框架内所有事件的基类。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>唯一标识：每个事件拥有全局唯一的ID，基于UUID生成</li>
 *   <li>时间溯源：记录事件发生的精确时间戳（毫秒级）</li>
 *   <li>上下文继承：支持从现有事件继承ID和时间戳，形成事件链</li>
 *   <li>序列化支持：实现序列化接口，支持跨进程事件传递</li>
 * </ul>
 *
 * @author soeasy.run
 * @see EventObject
 * @see Millisecond
 * @see UUIDSequence
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BaseEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    
    /** 事件唯一标识符，基于UUID生成，用于事件追踪和溯源 */
    @NonNull
    private final String id;
    
    /** 事件发生的时间戳（毫秒），基于系统时钟获取 */
    private final long timestamp;

    /**
     * 基于现有事件创建新事件（浅拷贝）
     * <p>
     * 新事件将继承原始事件的ID和时间戳，仅修改事件源。
     * 适用于需要保持事件链连续性的场景，如事件处理过程中的衍生事件。
     * 
     * @param event 原始事件，不可为null
     * @throws NullPointerException 若event为null
     */
    public BaseEvent(@NonNull BaseEvent event) {
        this(event.source, event);
    }

    /**
     * 使用事件源创建新事件（自动生成ID和时间戳）
     * <p>
     * 生成全局唯一的事件ID，并记录当前系统时间作为时间戳。
     * 适用于事件源头的创建场景，如初始事件触发。
     * 
     * @param source 事件源对象，不可为null
     * @throws NullPointerException 若source为null
     */
    public BaseEvent(@NonNull Object source) {
        this(source, UUIDSequence.random().next(), System.currentTimeMillis());
    }

    /**
     * 使用事件源和上下文事件创建新事件（继承ID和时间戳）
     * <p>
     * 新事件继承上下文事件的ID和时间戳，形成事件链关系，
     * 适用于事件处理过程中需要追溯原始事件的场景。
     * 
     * @param source 事件源对象，不可为null
     * @param context 上下文事件，不可为null
     * @throws NullPointerException 若source或context为null
     */
    public BaseEvent(@NonNull Object source, @NonNull BaseEvent context) {
        this(source, context.id, context.timestamp);
    }

    /**
     * 全参数构造事件（自定义ID和时间戳）
     * <p>
     * 允许自定义事件ID和时间戳，主要用于测试场景或需要精确控制事件时序的场景。
     * 
     * @param source 事件源对象，不可为null
     * @param id 事件唯一ID，不可为null
     * @param timestamp 事件时间戳（毫秒），建议使用{@link System#currentTimeMillis()}
     * @throws NullPointerException 若source或id为null
     */
    public BaseEvent(@NonNull Object source, @NonNull String id, long timestamp) {
        super(source);
        this.id = id;
        this.timestamp = timestamp;
    }

    /**
     * 事件的字符串表示，包含时间戳、事件ID和事件源信息
     * <p>
     * 使用{@link Millisecond}格式化时间戳，便于人类可读和日志记录。
     * 
     * @return 格式化的事件字符串
     */
    @Override
    public String toString() {
        return Millisecond.DEFAULT.format(timestamp) + " on event " + id + " source " + source;
    }
}