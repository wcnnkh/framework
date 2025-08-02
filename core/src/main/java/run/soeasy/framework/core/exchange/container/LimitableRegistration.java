package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import lombok.NonNull;
import run.soeasy.framework.core.concurrent.limit.Limiter;
import run.soeasy.framework.core.concurrent.limit.NoOpLimiter;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 可限制的注册基类，提供基于限流器的注册状态管理。
 * <p>
 * 该抽象类实现了{@link Registration}接口，通过集成{@link Limiter}机制，
 * 允许对注册的取消操作进行限制和控制，确保在特定条件下注册不可取消或需获取资源锁后才能取消。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>限流控制：使用限流器决定注册是否可被取消</li>
 *   <li>资源锁定：在取消操作时可获取资源锁，确保操作的原子性</li>
 *   <li>条件检查：支持通过{@link BooleanSupplier}提供额外的取消条件</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Registration
 * @see Limiter
 */
public abstract class LimitableRegistration implements Registration {
    /** 用于控制注册取消操作的限流器 */
    @NonNull
    private final Limiter limiter;

    /**
     * 默认构造函数，使用无操作限流器
     * <p>
     * 创建的注册对象将不受限流控制，可自由取消。
     */
    public LimitableRegistration() {
        this(new NoOpLimiter());
    }

    /**
     * 带限流器的构造函数
     * <p>
     * 使用指定的限流器控制注册的取消操作。
     * 
     * @param limiter 用于控制注册取消的限流器，不可为null
     */
    public LimitableRegistration(@NonNull Limiter limiter) {
        this.limiter = limiter;
    }

    /**
     * 获取当前注册使用的限流器
     * 
     * @return 限流器实例
     */
    public Limiter getLimiter() {
        return limiter;
    }

    /**
     * 检查注册是否可取消
     * <p>
     * 该方法调用{@link #isCancellable(BooleanSupplier)}并传入默认检查器，
     * 默认检查器始终返回true，因此结果完全取决于限流器状态。
     * 
     * @return true表示可取消，false表示不可取消
     */
    @Override
    public final boolean isCancellable() {
        return isCancellable(() -> true);
    }

    /**
     * 基于条件检查注册是否可取消
     * <p>
     * 注册可取消的条件为：限流器未限制且自定义检查器返回true。
     * 
     * @param checker 自定义检查器，提供额外的取消条件
     * @return true表示可取消，false表示不可取消
     */
    public boolean isCancellable(BooleanSupplier checker) {
        return !limiter.isLimited() && checker.getAsBoolean();
    }

    /**
     * 尝试取消注册
     * <p>
     * 该方法调用{@link #cancel(BooleanSupplier)}并传入默认取消操作，
     * 默认取消操作始终返回true，表示尝试执行取消。
     * 
     * @return true表示取消成功，false表示取消失败或不可取消
     */
    @Override
    public final boolean cancel() {
        return cancel(() -> true);
    }

    /**
     * 基于条件尝试取消注册
     * <p>
     * 取消流程：
     * 1. 检查限流器是否限制取消，若限制则直接返回false
     * 2. 尝试获取限流器的资源锁
     * 3. 获取锁成功后执行自定义取消操作
     * 4. 释放资源锁
     * 
     * @param cancel 自定义取消操作，返回取消结果
     * @return true表示取消成功，false表示取消失败或不可取消
     */
    public boolean cancel(BooleanSupplier cancel) {
        if (limiter.isLimited()) {
            return false;
        }

        Lock resource = limiter.getResource();
        if (resource.tryLock()) {
            try {
                return cancel.getAsBoolean();
            } finally {
                resource.unlock();
            }
        }
        return false;
    }

    /**
     * 检查注册是否已取消
     * <p>
     * 该方法调用{@link #isCancelled(BooleanSupplier)}并传入默认检查器，
     * 默认检查器始终返回false，因此结果完全取决于限流器状态。
     * 
     * @return true表示已取消，false表示未取消
     */
    @Override
    public final boolean isCancelled() {
        return isCancelled(() -> false);
    }

    /**
     * 基于条件检查注册是否已取消
     * <p>
     * 注册已取消的条件为：限流器限制取消或自定义检查器返回true。
     * 
     * @param checker 自定义检查器，提供额外的已取消条件
     * @return true表示已取消，false表示未取消
     */
    public boolean isCancelled(BooleanSupplier checker) {
        return limiter.isLimited() || checker.getAsBoolean();
    }
}