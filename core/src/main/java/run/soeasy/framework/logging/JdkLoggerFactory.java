package run.soeasy.framework.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * JDK日志工厂适配器，实现{@link LoggerFactory}接口，
 * 负责创建基于Java标准库{@link java.util.logging.Logger}的日志器实例，
 * 提供JDK日志系统与自定义日志接口的桥接功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>JDK日志桥接：创建{@link JdkLogger}实例，包装JDK原生Logger</li>
 *   <li>自动配置：无处理器时添加{@link ConsoleHandler}并禁用父处理器</li>
 *   <li>名称适配：直接使用JDK的Logger.getLogger(String)创建日志器</li>
 * </ul>
 * 
 * <p><b>初始化策略：</b>
 * <ul>
 *   <li>当JDK Logger无处理器时，添加ConsoleHandler确保日志输出</li>
 *   <li>设置{@code setUseParentHandlers(false)}避免继承父级处理器</li>
 *   <li>适用于需要独立配置的日志器场景</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see LoggerFactory
 * @see JdkLogger
 * @see java.util.logging.Logger
 */
public class JdkLoggerFactory implements LoggerFactory {

    /**
     * 创建JDK日志器适配器实例。
     * <p>
     * 调用JDK的{@link java.util.logging.Logger#getLogger(String)}获取原生Logger，
     * 无处理器时自动添加控制台处理器并禁用父处理器，
     * 最后包装为{@link JdkLogger}返回。
     * 
     * @param name 日志器名称（如类全限定名）
     * @return JdkLogger适配器实例，不可为null
     */
    @Override
    public run.soeasy.framework.logging.Logger getLogger(String name) {
        Logger jdkLogger = java.util.logging.Logger.getLogger(name);
        if (jdkLogger.getHandlers().length == 0) {
            jdkLogger.addHandler(new ConsoleHandler());
            jdkLogger.setUseParentHandlers(false);
        }
        return new JdkLogger(jdkLogger);
    }
}