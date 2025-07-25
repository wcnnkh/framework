package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 元素注册包装器，用于增强{@link ElementRegistration}的功能并提供透明转发。
 * <p>
 * 该类继承自{@link PayloadRegistrationWrapped}，实现了{@link ElementRegistrationWrapper}接口，
 * 允许将元素注册对象包装为具有额外功能的注册实例，同时保持与源注册对象的行为一致性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明转发：所有方法调用自动转发至被包装的源注册对象</li>
 *   <li>元素管理：继承有效载荷注册的同时支持元素生命周期控制</li>
 *   <li>注册组合：支持与其他注册对象的组合操作</li>
 *   <li>相关注册管理：维护与当前注册相关的其他注册集合</li>
 * </ul>
 *
 * @param <V> 元素载荷类型
 * @param <W> 被包装的源注册类型，需继承{@link ElementRegistration}&lt;{@link V}&gt;
 * 
 * @author soeasy.run
 * @see ElementRegistration
 * @see ElementRegistrationWrapper
 * @see PayloadRegistrationWrapped
 */
public class ElementRegistrationWrapped<V, W extends ElementRegistration<V>>
        extends PayloadRegistrationWrapped<V, W> implements ElementRegistrationWrapper<V, W> {

    /**
     * 构造函数，初始化元素注册包装器
     * <p>
     * 包装指定的元素注册对象，并关联相关注册集合。
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param relatedRegistrations 相关注册对象集合，不可为null
     * @throws NullPointerException 若source或relatedRegistrations为null
     */
    public ElementRegistrationWrapped(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
        super(source, relatedRegistrations);
    }

    /**
     * 复制构造函数，用于创建具有相同配置的新包装器
     * <p>
     * 从现有包装器复制上下文信息，创建新的元素注册包装器。
     * 
     * @param context 要复制的有效载荷注册包装器上下文
     */
    protected ElementRegistrationWrapped(PayloadRegistrationWrapped<V, W> context) {
        super(context);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 将当前注册与指定注册组合，返回新的元素注册包装器实例。
     * 新包装器将包含当前注册和指定注册的组合逻辑。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的元素注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public ElementRegistrationWrapped<V, W> and(@NonNull Registration registration) {
        return new ElementRegistrationWrapped<>(super.and(registration));
    }
}