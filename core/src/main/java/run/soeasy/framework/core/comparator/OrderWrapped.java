package run.soeasy.framework.core.comparator;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;

/**
 * 带顺序的包装器，为被包装对象添加顺序属性，实现{@link Ordered}接口以支持排序。
 * <p>
 * 该类继承自{@link Wrapped}，在包装源对象的同时赋予其顺序优先级，
 * 适用于需要按特定顺序处理对象的场景（如事件处理、处理器链等）。
 * </p>
 *
 * @param <W> 被包装的源对象类型
 * 
 * @author soeasy.run
 * @see Wrapped
 * @see Ordered
 */
@Getter
public class OrderWrapped<W> extends Wrapped<W> implements Ordered {
    /** 顺序优先级，数值越小优先级越高 */
    private final int order;

    /**
     * 构造函数，创建带顺序的包装器
     * 
     * @param source 被包装的源对象，不可为null
     * @param order 顺序优先级，数值越小越优先
     * @throws NullPointerException 若source为null
     */
    public OrderWrapped(@NonNull W source, int order) {
        super(source);
        this.order = order;
    }
}