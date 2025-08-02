package run.soeasy.framework.core.exchange;

/**
 * 生命周期处理器接口，定义处理ApplicationContext中Lifecycle组件的策略。
 * 该接口继承自{@link Lifecycle}，在基础生命周期操作之外，
 * 提供上下文刷新和关闭时的特殊处理能力。
 *
 * <p>核心职责：
 * <ul>
 *   <li>统一管理容器中所有Lifecycle组件的生命周期</li>
 *   <li>在上下文刷新时触发组件自动启动</li>
 *   <li>在上下文关闭时触发组件自动停止</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>容器启动时自动初始化所有可启动组件</li>
 *   <li>容器关闭时统一释放资源</li>
 *   <li>管理异步组件的启动顺序</li>
 *   <li>实现组件的延迟初始化策略</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Lifecycle
 */
public interface LifecycleProcessor extends Lifecycle {

    /**
     * 上下文刷新通知处理
     * 通常用于在应用上下文刷新完成后自动启动相关组件
     * 
     * <p>此方法会在ApplicationContext的refresh()过程中被调用，
     * 是组件初始化的最后阶段，此时所有Bean已加载完成
     */
    void onRefresh();

    /**
     * 上下文关闭通知处理
     * 通常用于在应用上下文关闭前自动停止相关组件
     * 
     * <p>此方法会在ApplicationContext的close()过程中被调用，
     * 是组件资源释放的第一阶段，先于常规的销毁回调
     */
    void onClose();
}