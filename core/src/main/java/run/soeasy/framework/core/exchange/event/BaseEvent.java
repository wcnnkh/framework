package run.soeasy.framework.core.exchange.event;

import java.util.EventObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.time.Millisecond;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

/**
 * 基础事件类
 * 定义事件的基本属性和行为，继承自Java的EventObject
 * 
 * @author soeasy.run
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BaseEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    
    /** 事件唯一标识符 */
    @NonNull
    private final String id;
    
    /** 事件发生的时间戳（毫秒） */
    private final long timestamp;

    /**
     * 基于现有事件创建新事件（浅拷贝）
     * 
     * @param event 原始事件，不可为null
     */
    public BaseEvent(@NonNull BaseEvent event) {
        this(event.source, event);
    }

    /**
     * 使用事件源创建新事件（自动生成ID和时间戳）
     * 
     * @param source 事件源对象，不可为null
     */
    public BaseEvent(@NonNull Object source) {
        this(source, UUIDSequences.global().next(), System.currentTimeMillis());
    }

    /**
     * 使用事件源和上下文事件创建新事件（继承ID和时间戳）
     * 
     * @param source 事件源对象，不可为null
     * @param context 上下文事件，不可为null
     */
    public BaseEvent(@NonNull Object source, @NonNull BaseEvent context) {
        this(source, context.id, context.timestamp);
    }

    /**
     * 全参数构造事件（自定义ID和时间戳）
     * 
     * @param source 事件源对象，不可为null
     * @param id 事件唯一ID，不可为null
     * @param timestamp 事件时间戳，单位：毫秒
     */
    public BaseEvent(@NonNull Object source, @NonNull String id, long timestamp) {
        super(source);
        this.id = id;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return Millisecond.DEFAULT.format(timestamp) + " on event " + id + " source " + source;
    }
}