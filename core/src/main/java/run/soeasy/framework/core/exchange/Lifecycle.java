package run.soeasy.framework.core.exchange;

/**
 * 生命周期管理接口，定义组件的基本生命周期操作。
 * 实现此接口的组件可以被统一管理其启动、停止状态和运行检查。
 *
 * <p>核心特性：
 * <ul>
 *   <li>状态管理：提供启动、停止和运行状态检查</li>
 *   <li>幂等操作：start()和stop()支持重复调用</li>
 *   <li>容器传播：容器实现应将生命周期操作传播到所有子组件</li>
 * </ul>
 *
 * <p>生命周期说明：
 * <ol>
 *   <li>初始状态：组件创建后处于未启动状态</li>
 *   <li>启动：调用start()方法后进入运行状态</li>
 *   <li>停止：调用stop()方法后进入停止状态</li>
 *   <li>销毁：可能在stop()后被调用，但不保证</li>
 * </ol>
 *
 * <p>注意事项：
 * <ul>
 *   <li>stop()不保证在destroy()之前调用</li>
 *   <li>实现类应妥善处理重复调用start()和stop()的情况</li>
 *   <li>容器实现应递归管理子组件的生命周期</li>
 * </ul>
 *
 * @author soeasy.run
 * @see SmartLifecycle
 */
public interface Lifecycle {

    /**
     * 启动组件
     * 设计为幂等操作，多次调用不应抛出异常
     * 
     * <p>对于容器组件，此操作应递归启动所有子组件
     * 
     * <p>调用此方法后，isRunning()应返回true
     */
    void start();

    /**
     * 停止组件
     * 设计为同步操作，方法返回时组件应完全停止
     * 设计为幂等操作，多次调用不应抛出异常
     * 
     * <p>注意：此方法不保证在组件销毁前被调用
     * 在正常关闭时，Lifecycle组件会先收到stop通知，再收到destroy通知
     * 但在上下文刷新或刷新失败时，可能直接调用destroy而不调用stop
     * 
     * <p>对于容器组件，此操作应递归停止所有子组件
     * 
     * <p>调用此方法后，isRunning()应返回false
     */
    void stop();

    /**
     * 检查组件是否正在运行
     * 
     * <p>对于容器组件，仅当所有子组件都在运行时才返回true
     * 
     * @return 如果组件正在运行返回true，否则返回false
     */
    boolean isRunning();
}