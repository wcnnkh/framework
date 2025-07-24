package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.ObjectUtils;

/**
 * 抽象负载注册实现，提供基于负载对象的相等性比较、哈希计算和字符串表示。
 * <p>
 * 该类继承自{@link AbstractLifecycleRegistration}，实现了{@link PayloadRegistration}接口，
 * 将注册对象的身份和行为与其承载的负载对象绑定，默认使用负载对象的属性进行相等性判断。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>基于负载的相等性：equals、hashCode和toString方法均委派给负载对象</li>
 *   <li>生命周期管理：继承自父类的启动、停止和状态管理能力</li>
 *   <li>可扩展性：允许子类重写默认行为</li>
 * </ul>
 * </p>
 *
 * @param <S> 负载对象的类型
 * 
 * @author soeasy.run
 * @see AbstractLifecycleRegistration
 * @see PayloadRegistration
 */
public abstract class AbstractPayloadRegistration<S> extends AbstractLifecycleRegistration
        implements PayloadRegistration<S> {

    /**
     * 基于负载对象的相等性判断
     * <p>
     * 该实现认为两个注册对象相等当且仅当其负载对象相等。
     * 特殊情况：若比较对象不是AbstractPayloadRegistration的实例，
     * 则直接比较当前注册的负载对象与该对象是否相等。
     * </p>
     * 
     * @param obj 要比较的对象
     * @return 若对象相等则返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractPayloadRegistration) {
            AbstractPayloadRegistration<?> other = (AbstractPayloadRegistration<?>) obj;
            return ObjectUtils.equals(this.getPayload(), other.getPayload());
        }
        // 允许与负载对象直接比较，增强灵活性
        return ObjectUtils.equals(this.getPayload(), obj);
    }

    /**
     * 基于负载对象的哈希值计算
     * 
     * @return 负载对象的哈希值
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(this.getPayload());
    }

    /**
     * 基于负载对象的字符串表示
     * 
     * @return 负载对象的字符串表示
     */
    @Override
    public String toString() {
        return ObjectUtils.toString(this.getPayload());
    }
}