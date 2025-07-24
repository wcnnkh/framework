package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 日志器装饰器类，继承自{@link AbstractLogger}并实现{@link Wrapper<Logger>}接口，
 * 采用装饰器模式包装目标日志器，提供日志级别过滤和动态切换功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>日志代理：将日志操作转发给被包装的源日志器{@link #source}</li>
 *   <li>级别过滤：在记录日志前检查当前级别是否允许记录</li>
 *   <li>动态切换：支持运行时更换被包装的源日志器{@link #setSource(Logger)}</li>
 *   <li>级别同步：设置级别时尝试同步到源日志器（若为{@link AbstractLogger}）</li>
 * </ul>
 * 
 * <p><b>线程安全说明：</b>
 * <ul>
 *   <li>源日志器引用使用volatile保证可见性</li>
 *   <li>日志记录操作线程安全委托给源日志器</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see AbstractLogger
 * @see Wrapper
 */
public class FacadeLogger extends AbstractLogger implements Wrapper<Logger> {
    /** 被包装的源日志器（volatile保证可见性） */
    private volatile Logger source;

    /**
     * 创建日志器装饰器实例。
     * 
     * @param source 被包装的源日志器，不可为null
     */
    public FacadeLogger(@NonNull Logger source) {
        this.source = source;
    }

    /**
     * 获取源日志器的名称。
     * <p>
     * 直接转发给源日志器的{@link Logger#getName()}方法。
     * 
     * @return 源日志器名称
     */
    @Override
    public String getName() {
        return source.getName();
    }

    /**
     * 获取被包装的源日志器。
     * 
     * @return 源日志器实例
     */
    public Logger getSource() {
        return source;
    }

    /**
     * 记录日志（实现{@link Logger#log(LogRecord)}）。
     * <p>
     * 先检查当前级别是否允许记录，再转发给源日志器。
     * 
     * @param record 日志记录对象
     */
    @Override
    public void log(LogRecord record) {
        if (isLoggable(record.getLevel())) {
            source.log(record);
        }
    }

    /**
     * 设置日志级别（覆盖父类方法）。
     * <p>
     * 先调用父类设置自身级别，再尝试设置源日志器的级别（若为{@link AbstractLogger}）。
     * 
     * @param level 新日志级别
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(source instanceof AbstractLogger) {
        	((AbstractLogger) source).setLevel(level);
        }
    }

    /**
     * 更换被包装的源日志器。
     * <p>
     * 同步更新源日志器引用，并记录级别变更日志。
     * 
     * @param source 新源日志器，不可为null
     */
    public void setSource(@NonNull Logger source) {
        Logger oldLogger = this.source;
        this.source = source;
        info("Logger change from {} to {}", oldLogger, this.source);
    }
}