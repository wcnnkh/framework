package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 具有生命周期管理能力的注册接口，集成注册功能与生命周期控制。
 * <p>
 * 该接口继承自{@link Registration}和{@link Lifecycle}，
 * 允许对注册对象进行生命周期管理（启动、停止），适用于需要生命周期控制的注册场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册功能：继承Registration的所有操作</li>
 *   <li>生命周期控制：提供start/stop/isRunning方法</li>
 *   <li>状态一致性：注册状态与生命周期状态联动</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Registration
 * @see Lifecycle
 */
public interface LifecycleRegistration extends Registration, Lifecycle {
    /**
     * 启动注册对象的生命周期
     * <p>
     * 该方法在注册成功后调用，用于激活注册对象的运行状态。
     * 调用后{@link #isRunning()}应返回true。
     */
    @Override
    void start();

    /**
     * 停止注册对象的生命周期
     * <p>
     * 该方法在注册失效后调用，用于停止注册对象的运行。
     * 调用后{@link #isRunning()}应返回false。
     */
    @Override
    void stop();

    /**
     * 检查注册对象的生命周期是否处于运行状态
     * <p>
     * @return true表示已调用start且未调用stop，false表示未启动或已停止
     */
    @Override
    boolean isRunning();
}