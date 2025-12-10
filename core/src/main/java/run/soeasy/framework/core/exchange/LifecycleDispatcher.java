package run.soeasy.framework.core.exchange;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生命周期事件调度器，管理生命周期状态并分发生命周期事件。
 * <p>
 * 该类继承自{@link EventDispatcher}，实现了{@link LifecycleProcessor}接口，
 * 提供了对生命周期状态（启动、停止、刷新、关闭）的管理能力，并在状态变更时发布对应的生命周期事件。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>原子性状态管理：使用{@link AtomicBoolean}确保状态变更的原子性和线程安全</li>
 *   <li>事件驱动：状态变更时自动发布{@link Lifecycle}事件</li>
 *   <li>幂等操作：通过CAS机制确保状态变更的幂等性，避免重复操作</li>
 *   <li>生命周期回调：实现{@link LifecycleProcessor}接口，支持生命周期事件处理</li>
 * </ul>
 *
 * @author soeasy.run
 * @see EventDispatcher
 * @see Lifecycle
 * @see LifecycleProcessor
 */
public class LifecycleDispatcher extends EventDispatcher<Lifecycle> implements LifecycleProcessor {
    
    /** 原子状态标志，记录当前生命周期状态（true表示已启动） */
    private final AtomicBoolean started = new AtomicBoolean();

    /**
     * 启动生命周期
     * <p>
     * 使用CAS机制确保只有首次调用有效（幂等性），状态变更后发布生命周期事件。
     * 多次调用start()不会产生副作用，仅首次调用会触发状态变更和事件发布。
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            this.publish(this);
        }
    }

    /**
     * 停止生命周期
     * <p>
     * 使用CAS机制确保只有从启动状态到停止状态的变更有效，状态变更后发布生命周期事件。
     * 多次调用stop()不会产生副作用，仅首次调用会触发状态变更和事件发布。
     */
    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            this.publish(this);
        }
    }

    /**
     * 检查生命周期是否正在运行
     * 
     * @return true表示生命周期已启动，false表示已停止
     */
    @Override
    public boolean isRunning() {
        return started.get();
    }

    /**
     * 刷新生命周期
     * <p>
     * 先发布刷新事件，再确保生命周期状态为启动状态（finally块）。
     * 该方法保证在事件处理完成后生命周期处于启动状态，适用于需要重启或重置的场景。
     */
    @Override
    public void onRefresh() {
        try {
            this.publish(this);
        } finally {
            started.set(true);
        }
    }

    /**
     * 关闭生命周期
     * <p>
     * 将生命周期状态设置为停止，并发布关闭事件。
     * 与stop()方法的区别：onClose()不检查状态变更，直接设置为停止并发布事件。
     */
    @Override
    public void onClose() {
        started.set(false);
        this.publish(this);
    }
}