package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 注册包装器，用于增强和扩展{@link Registration}的功能。
 * <p>
 * 该类继承自{@link InterceptableRegisration}，实现了{@link Wrapper}接口，
 * 允许将一个基础注册对象包装为具有额外功能的注册对象，如组合注册、条件取消等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册包装：通过构造函数传入被包装的源注册对象</li>
 *   <li>组合注册：支持与其他注册对象组合形成新的注册</li>
 *   <li>条件操作：支持通过{@link BooleanSupplier}提供条件逻辑</li>
 *   <li>相关注册管理：维护与当前注册相关的其他注册对象集合</li>
 * </ul>
 *
 * @param <W> 被包装的注册类型，需继承{@link Registration}
 * 
 * @author shuchaowen
 * @see Registration
 * @see Wrapper
 * @see InterceptableRegisration
 */
public class RegistrationWrapped<W extends Registration>
        extends InterceptableRegisration<Registration, Registration, Registration> implements Wrapper<W> {
    
    /** 被包装的源注册对象 */
    @NonNull
    private final W source;

    /**
     * 构造函数，初始化注册包装器
     * 
     * @param source 被包装的源注册对象，不可为null
     * @param relatedRegistrations 相关注册对象集合，不可为null
     * @throws NullPointerException 若source或relatedRegistrations为null
     */
    public RegistrationWrapped(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
        super(relatedRegistrations);
        this.source = source;
    }

    /**
     * 复制构造函数，用于创建具有相同配置的新包装器
     * 
     * @param wrapper 要复制的包装器
     */
    protected RegistrationWrapped(RegistrationWrapped<W> wrapper) {
        this(wrapper, wrapper.source);
    }

    /**
     * 内部构造函数，用于创建组合注册的新包装器
     * 
     * @param context 拦截器上下文
     * @param source 被包装的源注册对象
     */
    private RegistrationWrapped(InterceptableRegisration<Registration, Registration, Registration> context,
            W source) {
        super(context);
        this.source = source;
    }

    /**
     * 获取被包装的源注册对象
     * 
     * @return 源注册对象
     */
    @Override
    public W getSource() {
        return source;
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将当前注册与指定注册组合，返回新的注册包装器实例。
     * 
     * @param registration 要组合的注册对象
     * @return 组合后的注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public Registration and(Registration registration) {
        return combine(registration);
    }

    /**
     * 组合当前注册与另一个注册
     * <p>
     * 该方法将当前注册与指定注册组合，返回新的注册包装器实例。
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 组合后的注册包装器
     * @throws NullPointerException 若registration为null
     */
    @Override
    public RegistrationWrapped<W> combine(@NonNull Registration registration) {
        return new RegistrationWrapped<>(super.combine(registration), source);
    }

    /**
     * 批量组合当前注册与多个注册
     * <p>
     * 该方法将当前注册与指定的注册集合组合，返回新的注册包装器实例。
     * 
     * @param registrations 要组合的注册对象集合，不可为null
     * @return 组合后的注册包装器
     * @throws NullPointerException 若registrations为null
     */
    @Override
    public RegistrationWrapped<W> combineAll(@NonNull Elements<? extends Registration> registrations) {
        return new RegistrationWrapped<>(super.combineAll(registrations), source);
    }

    /**
     * 检查注册是否可取消
     * <p>
     * 该方法结合当前包装器的条件和源注册的条件判断是否可取消。
     * 
     * @param checker 额外的条件检查器
     * @return 若满足所有条件则返回true，否则返回false
     */
    @Override
    public boolean isCancellable(BooleanSupplier checker) {
        return super.isCancellable(() -> checker.getAsBoolean() || source.isCancellable());
    }

    /**
     * 检查注册是否已取消
     * <p>
     * 该方法结合当前包装器的条件和源注册的状态判断是否已取消。
     * 
     * @param checker 额外的条件检查器
     * @return 若满足所有条件则返回true，否则返回false
     */
    @Override
    public boolean isCancelled(BooleanSupplier checker) {
        return super.isCancelled(() -> checker.getAsBoolean() && source.isCancelled());
    }

    /**
     * 尝试取消注册
     * <p>
     * 该方法结合当前包装器的条件和源注册的取消操作执行取消。
     * 
     * @param cancel 额外的取消条件
     * @return 若成功取消则返回true，否则返回false
     */
    @Override
    public boolean cancel(BooleanSupplier cancel) {
        return super.cancel(() -> cancel.getAsBoolean() && source.cancel());
    }
}