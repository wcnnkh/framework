package run.soeasy.framework.logging;

/**
 * 日志器工厂接口，负责创建{@link Logger}实例，
 * 提供统一的日志器创建入口，支持通过名称获取特定日志器。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>日志器创建：根据名称创建对应的Logger实例</li>
 *   <li>命名规范：支持以类名、模块名等作为日志器名称</li>
 *   <li>实例管理：可能包含日志器实例的缓存或生命周期管理</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>应用初始化：在启动时通过工厂获取日志器</li>
 *   <li>模块化日志：按模块名称获取专属日志器</li>
 *   <li>日志适配：适配不同日志实现（如Java Logging、Log4j等）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Logger
 */
public interface LoggerFactory {
    /**
     * 根据名称获取日志器实例。
     * <p>
     * 实现类应根据名称规则（如类全限定名、模块名）创建或获取日志器，
     * 推荐使用类名作为日志器名称以区分不同组件的日志。
     * 
     * @param name 日志器名称（如类全限定名"com.example.App"）
     * @return 对应的Logger实例，不可为null
     */
    Logger getLogger(String name);
}