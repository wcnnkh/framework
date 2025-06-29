package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 可拦截的注册
 * 支持为注册操作添加前置和后置处理逻辑，形成拦截器链
 * 
 * @author soeasy.run
 *
 * @param <B> 前置处理注册类型
 * @param <T> 主处理注册类型
 * @param <A> 后置处理注册类型
 */
public class InterceptableRegisration<B extends Registration, T extends Registration, A extends Registration>
        extends LimitableRegistrations<T> {
    
    /**
     * 前置处理注册集合
     */
    private final LimitableRegistrations<B> preRegistration;
    
    /**
     * 后置处理注册集合
     */
    private final LimitableRegistrations<A> postRegistration;

    /**
     * 私有构造函数，用于创建完整的可拦截注册实例
     * 
     * @param combinableRegistration 主注册集合
     * @param preRegistration 前置处理注册集合
     * @param postRegistration 后置处理注册集合
     */
    private InterceptableRegisration(LimitableRegistrations<T> combinableRegistration,
            LimitableRegistrations<B> preRegistration, LimitableRegistrations<A> postRegistration) {
        super(combinableRegistration);
        this.preRegistration = preRegistration;
        this.postRegistration = postRegistration;
    }

    /**
     * 受保护的构造函数，用于创建基于现有实例的副本
     * 
     * @param interceptableRegisration 现有可拦截注册实例，不可为null
     */
    protected InterceptableRegisration(@NonNull InterceptableRegisration<B, T, A> interceptableRegisration) {
        this(interceptableRegisration, interceptableRegisration.preRegistration,
                interceptableRegisration.postRegistration);
    }

    /**
     * 公共构造函数，创建带有空前置和后置处理的可拦截注册
     * 
     * @param registrations 主注册集合，不可为null
     */
    public InterceptableRegisration(@NonNull Elements<T> registrations) {
        super(registrations);
        this.preRegistration = new LimitableRegistrations<>(Elements.empty());
        this.postRegistration = new LimitableRegistrations<>(Elements.empty());
    }

    @Override
    public InterceptableRegisration<B, T, A> combine(@NonNull T registration) {
        return new InterceptableRegisration<>(super.combine(registration), this.preRegistration, this.postRegistration);
    }

    @Override
    public InterceptableRegisration<B, T, A> combineAll(@NonNull Elements<? extends T> registrations) {
        return new InterceptableRegisration<>(super.combineAll(registrations), this.preRegistration,
                this.postRegistration);
    }

    @Override
    public boolean cancel(BooleanSupplier cancel) {
        return super.cancel(() -> {
            // 先取消前置处理
            if (preRegistration.isCancellable()) {
                preRegistration.cancel();
            }

            try {
                // 执行主取消逻辑
                return cancel.getAsBoolean();
            } finally {
                // 最后取消后置处理
                if (postRegistration.isCancellable()) {
                    postRegistration.cancel();
                }
            }
        });
    }

    @Override
    public boolean isCancellable(BooleanSupplier checker) {
        return super.isCancellable(
                () -> preRegistration.isCancellable() || checker.getAsBoolean() || postRegistration.isCancellable());
    }

    @Override
    public boolean isCancelled(BooleanSupplier checker) {
        return super.isCancelled(
                () -> preRegistration.isCancelled() && checker.getAsBoolean() && postRegistration.isCancelled());
    }

    @Override
    public <R extends Registration> InterceptableRegisration<B, R, A> map(
            @NonNull Function<? super T, ? extends R> mapper) {
        return new InterceptableRegisration<>(super.map(mapper), this.preRegistration, this.postRegistration);
    }

    /**
     * 添加后置处理注册
     * 
     * @param registration 后置处理注册，不可为null
     * @return 新的可拦截注册实例，包含原注册和新的后置处理
     */
    public InterceptableRegisration<B, T, A> post(@NonNull A registration) {
        return new InterceptableRegisration<>(this, this.preRegistration, this.postRegistration.combine(registration));
    }

    /**
     * 添加前置处理注册
     * 
     * @param registration 前置处理注册，不可为null
     * @return 新的可拦截注册实例，包含原注册和新的前置处理
     */
    public InterceptableRegisration<B, T, A> pre(@NonNull B registration) {
        return new InterceptableRegisration<>(this, this.preRegistration.combine(registration), this.postRegistration);
    }
}