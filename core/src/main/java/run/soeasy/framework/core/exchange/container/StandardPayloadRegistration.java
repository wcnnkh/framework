package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 标准有效载荷注册实现，为注册对象附加有效载荷数据。
 * <p>
 * 该类继承自{@link RegistrationWrapped}，实现了{@link PayloadRegistration}接口，
 * 允许为注册对象包装有效载荷数据，适用于需要在注册过程中携带附加信息的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册包装：包装基础注册对象并附加有效载荷</li>
 *   <li>载荷访问：通过{@link #getPayload()}获取附加的载荷数据</li>
 *   <li>注册组合：支持与其他注册对象的组合操作</li>
 * </ul>
 *
 * @param <W> 被包装的注册类型，需继承{@link Registration}
 * @param <T> 有效载荷的类型
 * 
 * @author soeasy.run
 * @see PayloadRegistration
 * @see RegistrationWrapped
 */
public class StandardPayloadRegistration<W extends Registration, T> extends RegistrationWrapped<W>
        implements PayloadRegistration<T> {
    
    /** 附加的有效载荷数据 */
    private final T payload;

    /**
     * 构造函数，初始化标准有效载荷注册
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param payload 附加的有效载荷数据，可为null
     * @throws NullPointerException 若source为null
     */
    public StandardPayloadRegistration(@NonNull W source, T payload) {
        super(source, Elements.empty());
        this.payload = payload;
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将调用转发至{@link PayloadRegistration#and(Registration)}的默认实现，
     * 并返回组合后的有效载荷注册实例。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的有效载荷注册
     */
    @Override
    public PayloadRegistration<T> and(Registration registration) {
        return PayloadRegistration.super.and(registration);
    }

    /**
     * 获取附加的有效载荷数据
     * 
     * @return 注册附带的有效载荷数据
     */
    @Override
    public T getPayload() {
        return payload;
    }
}