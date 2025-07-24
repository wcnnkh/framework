package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;

/**
 * 原子性元素注册实现，提供对元素负载的线程安全操作和生命周期管理。
 * <p>
 * 该类继承自{@link AbstractPayloadRegistration}，使用{@link AtomicReference}确保对负载的原子性操作，
 * 实现了{@link ElementRegistration}接口，支持元素负载的获取、设置及生命周期控制。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>线程安全：通过{@link AtomicReference}保证负载操作的原子性和内存可见性</li>
 *   <li>负载管理：支持原子性获取和更新元素负载，避免竞态条件</li>
 *   <li>状态限制：继承父类的限制器机制，在注册被限制时拒绝负载修改</li>
 * </ul>
 *
 * @param <E> 元素负载的类型，需确保类型安全
 * 
 * @author soeasy.run
 * @see AbstractPayloadRegistration
 * @see ElementRegistration
 * @see AtomicReference
 */
@Getter
public class AtomicElementRegistration<E> extends AbstractPayloadRegistration<E> implements ElementRegistration<E> {
    
    /** 原子引用容器，确保负载操作的线程安全性和原子性 */
    private final AtomicReference<E> payloadReference;

    /**
     * 初始化带有指定初始值的原子元素注册
     * <p>
     * 构造时创建原子引用并存储初始负载值，确保初始状态的线程安全。
     * 
     * @param initialValue 元素的初始负载值，允许为null
     */
    public AtomicElementRegistration(E initialValue) {
        this.payloadReference = new AtomicReference<>(initialValue);
    }

    /**
     * 获取当前注册的元素负载
     * <p>
     * 通过原子引用安全获取当前负载值，支持多线程环境下的并发读取。
     * 
     * @return 当前存储的元素负载，可能为null
     */
    @Override
    public E getPayload() {
        return payloadReference.get();
    }

    /**
     * 原子性更新元素负载并返回旧值
     * <p>
     * 仅当注册未被限制时允许更新（通过父类限制器校验），否则抛出异常。
     * 更新操作使用原子引用保证线程安全，避免更新过程中的竞态条件。
     * 
     * @param payload 新的元素负载值，允许为null
     * @return 更新前的旧负载值，可能为null
     * @throws UnsupportedOperationException 当注册被限制（如已取消）时抛出
     */
    @Override
    public E setPayload(E payload) {
        if (getLimiter().isLimited()) {
            throw new UnsupportedOperationException("Registration is limited and cannot modify payload");
        }
        return payloadReference.getAndSet(payload);
    }
}