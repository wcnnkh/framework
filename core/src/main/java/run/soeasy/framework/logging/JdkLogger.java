package run.soeasy.framework.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * JDK日志适配器，继承自{@link AbstractLogger}，
 * 将Java标准库的{@link java.util.logging.Logger}适配为自定义{@link Logger}接口，
 * 实现日志级别同步、消息格式化和处理程序配置功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>JDK日志桥接：包装标准Logger，实现自定义日志接口的无缝对接</li>
 *   <li>级别同步：设置级别时自动同步到JDK Logger及其所有处理程序</li>
 *   <li>消息格式化：使用{@link FormatableMessage}处理参数化消息</li>
 *   <li>处理程序管理：递归配置父级Logger的处理程序级别</li>
 * </ul>
 * 
 * <p><b>实现细节：</b>
 * <ul>
 *   <li>日志记录：先格式化消息再调用JDK Logger的log方法</li>
 *   <li>级别检查：同时验证自定义级别和JDK级别的可记录性</li>
 *   <li>父级同步：当useParentHandlers为true时，递归配置父级Logger</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see AbstractLogger
 * @see java.util.logging.Logger
 */
public class JdkLogger extends AbstractLogger {
    /** 被包装的JDK标准Logger实例 */
    private final Logger logger;

    /**
     * 创建JDK日志适配器实例。
     * 
     * @param logger JDK标准Logger实例，不可为null
     */
    public JdkLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * 获取日志器名称（转发给JDK Logger）。
     * 
     * @return JDK Logger的名称
     */
    @Override
    public String getName() {
        return logger.getName();
    }

    /**
     * 获取被包装的JDK Logger实例。
     * 
     * @return JDK标准Logger实例
     */
    public Logger getTargetLogger() {
        return logger;
    }

    /**
     * 判断指定级别是否可记录（双重检查）。
     * <p>
     * 同时检查自定义级别配置和JDK Logger的级别设置，
     * 确保只有两者都允许时才记录日志。
     * 
     * @param level 待检查的日志级别
     * @return true表示可记录
     */
    @Override
    public boolean isLoggable(Level level) {
        return super.isLoggable(level) && logger.isLoggable(level);
    }

    /**
     * 记录日志（消息格式化后转发）。
     * <p>
     * 使用{@link FormatableMessage}格式化参数化消息，
     * 清除参数后调用JDK Logger的log方法。
     * 
     * @param record 日志记录对象
     */
    @Override
    public void log(LogRecord record) {
        String message = FormatableMessage.formatPlaceholder(record.getMessage(), null, record.getParameters());
        record.setMessage(message);
        record.setParameters(null);
        logger.log(record);
    }

    /**
     * 设置日志级别并同步到JDK组件。
     * <p>
     * 1. 调用父类设置自定义级别
     * 2. 设置JDK Logger的级别
     * 3. 递归配置所有处理程序和父级Logger的级别
     * 
     * @param level 新日志级别
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        logger.setLevel(level);
        Logger parent = this.logger;
        while (parent != null) {
            for (Handler handler : parent.getHandlers()) {
                handler.setLevel(level);
            }
            if (parent.getUseParentHandlers()) {
                parent = parent.getParent();
            } else {
                break;
            }
        }
    }
}