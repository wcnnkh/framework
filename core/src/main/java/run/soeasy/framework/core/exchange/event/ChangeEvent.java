package run.soeasy.framework.core.exchange.event;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;

/**
 * 变更事件类
 * 表示对象状态变更的事件，继承自BaseEvent
 * 
 * @author shuchaowen
 * @param <T> 变更对象的类型
 */
@Getter
public class ChangeEvent<T> extends BaseEvent {
    private static final long serialVersionUID = 1L;

    /**
     * 检查变更前后的值是否合法（不能同时为空）
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @throws IllegalArgumentException 当before和after同时为空时抛出
     */
    private static void check(Object before, Object after) {
        if (before == null && after == null) {
            throw new IllegalArgumentException("Cannot be empty before and after the change");
        }
    }

    /**
     * 根据变更前后的值确定变更类型
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 变更类型（创建、删除或更新）
     */
    private static ChangeType getChangeType(Object before, Object after) {
        if (before == null && after != null) {
            return ChangeType.CREATE;
        } else if (before != null && after == null) {
            return ChangeType.DELETE;
        } else {
            return ChangeType.UPDATE;
        }
    }

    /**
     * 选择事件源对象（优先使用after值）
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 事件源对象
     */
    private static <T> T selectSource(T before, T after) {
        check(before, after);
        // 优先使用after，因为这表示当前值
        return after == null ? before : after;
    }

    /**
     * 选择被更新的源对象（优先使用before值）
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 被更新的源对象
     */
    private static <T> T selectUpdatedSource(T before, T after) {
        if (before == null && after == null) {
            throw new IllegalArgumentException("Cannot be empty before and after the change");
        }

        // 选择before，因为这表示被更新的值
        return before == null ? after : before;
    }

    /** 变更类型（创建、删除或更新） */
    private final ChangeType changeType;

    /** 变更前的源对象（仅在更新时有效） */
    private final T updatedSource;

    /**
     * 浅拷贝构造函数
     * 
     * @param changeEvent 原始变更事件
     */
    public ChangeEvent(ChangeEvent<T> changeEvent) {
        super(changeEvent);
        this.changeType = changeEvent.changeType;
        this.updatedSource = changeEvent.updatedSource;
    }

    /**
     * 基于基础事件创建变更事件
     * 
     * @param event 基础事件
     * @param source 事件源
     * @param updateSource 被更新的源
     * @param changeType 变更类型
     */
    private ChangeEvent(BaseEvent event, T source, T updateSource, ChangeType changeType) {
        super(updateSource, event);
        this.changeType = changeType;
        this.updatedSource = updateSource;
    }

    /**
     * 构造指定类型的变更事件
     * 
     * @param source 事件源对象，不可为null
     * @param changeType 变更类型，不可为null
     */
    public ChangeEvent(@NonNull T source, @NonNull ChangeType changeType) {
        super(source);
        this.changeType = changeType;
        this.updatedSource = null;
    }

    /**
     * 根据变更前后的值构造变更事件
     * 
     * @param beforeSource 变更前的值
     * @param afterSource 变更后的值
     */
    public ChangeEvent(T beforeSource, T afterSource) {
        super(selectSource(beforeSource, afterSource));
        this.updatedSource = selectUpdatedSource(beforeSource, afterSource);
        this.changeType = getChangeType(beforeSource, afterSource);
    }

    /**
     * 转换变更事件的类型
     * 
     * @param <R> 目标类型
     * @param mapper 类型转换函数，不可为null
     * @return 转换后的变更事件
     */
    public <R> ChangeEvent<R> convert(@NonNull Function<? super T, ? extends R> mapper) {
        R targetSource = mapper.apply(getSource());
        R targetUpdateSource = updatedSource == null ? null : mapper.apply(updatedSource);
        return new ChangeEvent<R>(this, targetSource, targetUpdateSource, changeType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}