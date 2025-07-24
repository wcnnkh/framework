package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;

import run.soeasy.framework.core.concurrent.limit.DisposableLimiter;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.event.LifecycleDispatcher;

/**
 * 抽象生命周期注册基类，实现可管理生命周期的注册对象。
 * <p>
 * 该类继承自{@link LimitableRegistration}，提供了生命周期管理能力，
 * 实现了{@link LifecycleRegistration}和{@link Listenable}接口，
 * 允许注册生命周期监听器并管理注册对象的生命周期状态。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>生命周期管理：支持启动、停止和状态查询</li>
 *   <li>监听器机制：允许注册生命周期事件监听器</li>
 *   <li>一次性限制：使用{@link DisposableLimiter}确保注册只能被取消一次</li>
 *   <li>状态一致性：取消操作自动触发停止生命周期</li>
 * </ul>
 *
 * @author soeasy.run
 * @see LifecycleRegistration
 * @see Listenable
 * @see LimitableRegistration
 */
public abstract class AbstractLifecycleRegistration extends LimitableRegistration
        implements LifecycleRegistration, Listenable<Lifecycle> {
    
    /** 生命周期事件分发器，负责管理和通知生命周期监听器 */
    private final LifecycleDispatcher dispatcher = new LifecycleDispatcher();

    /**
     * 默认构造函数，初始化一次性限制器
     * <p>
     * 使用{@link DisposableLimiter}确保注册只能被取消一次，
     * 符合大多数生命周期管理场景的需求。
     */
    public AbstractLifecycleRegistration() {
        super(new DisposableLimiter());
    }

    /**
     * 注册生命周期监听器
     * <p>
     * 监听器将在生命周期状态变化时收到通知。
     * 
     * @param listener 要注册的生命周期监听器，不可为null
     * @return 注册句柄，可用于取消监听器注册
     */
    @Override
    public Registration registerListener(Listener<Lifecycle> listener) {
        return dispatcher.registerListener(listener);
    }

    /**
     * 尝试取消注册，并确保生命周期停止
     * <p>
     * 在父类取消逻辑执行后，无论结果如何都会调用{@link #stop()}方法确保生命周期停止。
     * 
     * @param cancel 自定义取消条件提供者
     * @return 取消操作结果，true表示成功，false表示失败
     */
    @Override
    public boolean cancel(BooleanSupplier cancel) {
        try {
            return super.cancel(cancel);
        } finally {
            stop();
        }
    }

    /**
     * 启动生命周期
     * <p>
     * 若注册已被取消，将抛出{@link IllegalStateException}。
     * 启动后，所有注册的监听器将收到启动事件通知。
     * 
     * @throws IllegalStateException 若注册已被取消
     */
    @Override
    public void start() {
        if (isCancelled()) {
            throw new IllegalStateException("Registration Cancelled");
        }

        dispatcher.start();
    }

    /**
     * 停止生命周期
     * <p>
     * 停止后，所有注册的监听器将收到停止事件通知。
     */
    @Override
    public void stop() {
        dispatcher.stop();
    }

    /**
     * 检查生命周期是否正在运行
     * 
     * @return true表示正在运行，false表示已停止
     */
    @Override
    public boolean isRunning() {
        return dispatcher.isRunning();
    }
}