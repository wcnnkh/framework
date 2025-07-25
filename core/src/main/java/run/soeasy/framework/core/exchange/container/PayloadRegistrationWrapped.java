package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 有效载荷注册包装器，用于增强和扩展{@link PayloadRegistration}的功能。
 * <p>
 * 该类继承自{@link RegistrationWrapped}，实现了{@link PayloadRegistrationWrapper}接口，
 * 允许将一个基础有效载荷注册对象包装为具有额外功能的注册对象，如组合注册、条件取消等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>有效载荷管理：继承源注册对象的载荷数据</li>
 *   <li>注册包装：通过构造函数传入被包装的源注册对象</li>
 *   <li>组合注册：支持与其他注册对象组合形成新的注册</li>
 *   <li>相关注册管理：维护与当前注册相关的其他注册对象集合</li>
 * </ul>
 *
 * @param <S> 有效载荷的类型
 * @param <W> 被包装的注册类型，需继承{@link PayloadRegistration}&lt;{@link S}&gt;
 * 
 * @author soeasy.run
 * @see PayloadRegistration
 * @see PayloadRegistrationWrapper
 * @see RegistrationWrapped
 */
public class PayloadRegistrationWrapped<S, W extends PayloadRegistration<S>>
        extends RegistrationWrapped<W> implements PayloadRegistrationWrapper<S, W> {

    /**
     * 构造函数，初始化有效载荷注册包装器
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param relatedRegistrations 相关注册对象集合，不可为null
     * @throws NullPointerException 若source或relatedRegistrations为null
     */
    public PayloadRegistrationWrapped(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
        super(source, relatedRegistrations);
    }

    /**
     * 复制构造函数，用于创建具有相同配置的新包装器
     * 
     * @param context 要复制的注册包装器上下文
     */
    protected PayloadRegistrationWrapped(RegistrationWrapped<W> context) {
        super(context);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将当前注册与指定注册组合，返回新的有效载荷注册包装器实例。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的有效载荷注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public PayloadRegistrationWrapped<S, W> and(@NonNull Registration registration) {
        return combine(registration);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将当前注册与指定注册组合，返回新的有效载荷注册包装器实例。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的有效载荷注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public PayloadRegistrationWrapped<S, W> combine(@NonNull Registration registration) {
        return new PayloadRegistrationWrapped<>(super.combine(registration));
    }
}