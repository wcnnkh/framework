package run.soeasy.framework.core.exchange.event;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;

/**
 * 变更事件类，用于表示对象状态变更的事件，继承自{@link BaseEvent}。
 * <p>
 * 该类封装了对象变更的上下文信息，包括变更前后的状态、变更类型等，
 * 适用于领域驱动设计中的领域事件、数据变更通知等场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>自动识别变更类型：根据变更前后的值自动判断是创建、删除还是更新</li>
 *   <li>类型安全转换：通过{@link #convert(Function)}方法支持事件类型转换</li>
 *   <li>源对象智能选择：根据变更类型自动选择合适的事件源对象</li>
 *   <li>参数校验：确保变更前后的值不同时为空，保证事件语义合法性</li>
 * </ul>
 *
 * @param <T> 变更对象的类型
 * 
 * @author soeasy.run
 * @see BaseEvent
 * @see ChangeType
 */
@Getter
public class ChangeEvent<T> extends BaseEvent {
    private static final long serialVersionUID = 1L;

    /**
     * 检查变更前后的值是否合法（不能同时为空）
     * <p>
     * 确保变更事件具有有效的语义，避免无意义的变更通知。
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @throws IllegalArgumentException 当before和after同时为空时抛出
     */
    private static void check(Object before, Object after) {
        if (before == null && after == null) {
            throw new IllegalArgumentException("变更前后的值不能同时为空");
        }
    }

    /**
     * 根据变更前后的值确定变更类型
     * <p>
     * 逻辑规则：
     * <ul>
     *   <li>before为null且after不为null：创建事件</li>
     *   <li>before不为null且after为null：删除事件</li>
     *   <li>其他情况：更新事件</li>
     * </ul>
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 对应的{@link ChangeType}枚举值
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
     * <p>
     * 事件源通常表示当前状态的对象，因此优先使用变更后的值。
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 事件源对象（after优先，若after为null则使用before）
     * @throws IllegalArgumentException 当before和after同时为空时抛出
     */
    private static <T> T selectSource(T before, T after) {
        check(before, after);
        // 优先使用after，因为其表示当前状态
        return after == null ? before : after;
    }

    /**
     * 选择被更新的源对象（优先使用before值）
     * <p>
     * 被更新的源对象表示变更前的实体，因此优先使用变更前的值。
     * 
     * @param before 变更前的值
     * @param after 变更后的值
     * @return 被更新的源对象（before优先，若before为null则使用after）
     * @throws IllegalArgumentException 当before和after同时为空时抛出
     */
    private static <T> T selectUpdatedSource(T before, T after) {
        check(before, after);
        // 选择before以表示被变更的原始对象
        return before == null ? after : before;
    }

    /** 变更类型（创建、删除或更新），不可变 */
    private final ChangeType changeType;

    /** 变更前的源对象（仅在更新时有效），不可变 */
    private final T updatedSource;

    /**
     * 浅拷贝构造函数，用于事件类型转换或上下文传递
     * 
     * @param changeEvent 原始变更事件，不可为null
     */
    public ChangeEvent(ChangeEvent<T> changeEvent) {
        super(changeEvent);
        this.changeType = changeEvent.changeType;
        this.updatedSource = changeEvent.updatedSource;
    }

    /**
     * 基于基础事件创建变更事件（内部使用）
     * 
     * @param event 基础事件，不可为null
     * @param source 事件源对象
     * @param updateSource 被更新的源对象
     * @param changeType 变更类型，不可为null
     */
    private ChangeEvent(BaseEvent event, T source, T updateSource, ChangeType changeType) {
        super(updateSource, event);
        this.changeType = changeType;
        this.updatedSource = updateSource;
    }

    /**
     * 构造指定类型的变更事件（手动指定变更类型）
     * 
     * @param source 事件源对象，不可为null
     * @param changeType 变更类型，不可为null
     * @throws NullPointerException 若source或changeType为null
     */
    public ChangeEvent(@NonNull T source, @NonNull ChangeType changeType) {
        super(source);
        this.changeType = changeType;
        this.updatedSource = null;
    }

    /**
     * 根据变更前后的值自动构造变更事件（推荐使用）
     * <p>
     * 自动识别变更类型并设置事件源，适用于大多数变更场景。
     * 
     * @param beforeSource 变更前的值
     * @param afterSource 变更后的值
     * @throws IllegalArgumentException 当before和after同时为空时抛出
     */
    public ChangeEvent(T beforeSource, T afterSource) {
        super(selectSource(beforeSource, afterSource));
        this.updatedSource = selectUpdatedSource(beforeSource, afterSource);
        this.changeType = getChangeType(beforeSource, afterSource);
    }

    /**
     * 转换变更事件的类型（支持事件类型升级或降级）
     * 
     * @param <R> 目标类型
     * @param mapper 类型转换函数，不可为null
     * @return 转换后的变更事件，保持原事件的上下文信息
     * @throws NullPointerException 若mapper为null
     */
    public <R> ChangeEvent<R> convert(@NonNull Function<? super T, ? extends R> mapper) {
        R targetSource = mapper.apply(getSource());
        R targetUpdateSource = updatedSource == null ? null : mapper.apply(updatedSource);
        return new ChangeEvent<R>(this, targetSource, targetUpdateSource, changeType);
    }

    /**
     * 获取强类型的事件源对象
     * 
     * @return 泛型类型的事件源对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}