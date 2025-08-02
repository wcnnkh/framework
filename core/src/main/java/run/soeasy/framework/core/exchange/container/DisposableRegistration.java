package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;

import lombok.NonNull;

/**
 * 一次性注册实现，在取消时执行指定的处理逻辑。
 * <p>
 * 该类继承自{@link AbstractLifecycleRegistration}，
 * 允许在注册被取消时执行特定的清理或处理操作，
 * 确保资源释放或状态恢复。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>一次性执行：确保取消时的处理逻辑仅执行一次</li>
 *   <li>生命周期管理：继承父类的生命周期管理能力</li>
 *   <li>自定义处理：通过构造函数传入取消时的处理逻辑</li>
 * </ul>
 *
 * @author soeasy.run
 * @see AbstractLifecycleRegistration
 */
public class DisposableRegistration extends AbstractLifecycleRegistration {
    /** 注册取消时执行的处理逻辑 */
    @NonNull
    private final Runnable runnable;

    /**
     * 构造函数，初始化一次性注册
     * <p>
     * 指定注册取消时要执行的处理逻辑。
     * 
     * @param runnable 注册取消时执行的处理逻辑，不可为null
     */
    public DisposableRegistration(@NonNull Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * 尝试取消注册，并执行预先指定的处理逻辑
     * <p>
     * 在父类取消逻辑的基础上，增加自定义处理逻辑的执行。
     * 只有当父类取消条件和传入的条件都满足时，才会执行处理逻辑。
     * 
     * @param cancel 自定义取消条件提供者
     * @return 取消操作结果，true表示成功，false表示失败
     */
    @Override
    public boolean cancel(BooleanSupplier cancel) {
        return super.cancel(() -> {
            if (cancel.getAsBoolean()) {
                runnable.run();
                return true;
            }
            return false;
        });
    }
}