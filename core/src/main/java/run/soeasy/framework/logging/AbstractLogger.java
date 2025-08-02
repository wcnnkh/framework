package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 抽象日志器实现类，提供{@link Logger}接口的基础实现，
 * 封装日志记录的核心逻辑和通用功能，降低具体日志器实现的复杂度。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>日志级别管理：支持动态设置和检查日志级别（{@link #setLevel(Level)}）</li>
 *   <li>调用者推断：可选推断日志调用者的类名和方法（通过{@link #needToInferCaller}配置）</li>
 *   <li>日志记录增强：重写{@link #createRecord(Level, Throwable, String, Object...)}添加调用者信息</li>
 *   <li>名称智能识别：自动判断日志器名称是否为类名（{@link #isNameToClass()}）</li>
 * </ul>
 * 
 * <p><b>实现说明：</b>
 * <ul>
 *   <li>线程安全：级别和推断开关使用volatile保证可见性</li>
 *   <li>性能考虑：调用者推断功能默认关闭，开启时存在性能开销</li>
 *   <li>扩展点：子类需实现{@link #log(LogRecord)}完成实际日志输出</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Logger
 * @see FacadeLogger
 */
@Getter
@Setter
public abstract class AbstractLogger implements Logger {
    /**
     * 系统属性控制是否需要推断调用者（默认读取io.basc.framework.logger.need.to.infer.caller）
     * <p>
     * 开启时会通过StackTrace获取调用者信息，存在性能开销，默认关闭
     */
    private static final boolean NEED_TO_INFER_CALLER = Boolean
            .getBoolean("io.basc.framework.logger.need.to.infer.caller");
    
    /** 日志器名称是否视为类名的缓存标志 */
    private Boolean isNameToClass;
    /** 日志级别（volatile保证线程可见性） */
    private volatile Level level;
    /** 是否需要推断调用者信息（volatile保证线程可见性） */
    private volatile boolean needToInferCaller = NEED_TO_INFER_CALLER;

    /**
     * 设置日志级别并记录级别变更日志。
     * <p>
     * 若级别变更，会记录INFO级别的变更日志，
     * 新级别通过volatile保证其他线程可见。
     * 
     * @param level 新日志级别
     */
    public void setLevel(Level level) {
        if (this.level != level) {
            info("Log level changed from {} to {}", this.level, level);
        }
        this.level = level;
    }

    /**
     * 创建增强的日志记录对象（重写接口方法）。
     * <p>
     * 在接口默认实现基础上，根据配置添加调用者类名和方法信息：
     * <ul>
     *   <li>needToInferCaller=true：通过栈追踪获取实际调用者</li>
     *   <li>isNameToClass=true：直接使用日志器名称作为类名</li>
     * </ul>
     * 
     * @param level 日志级别
     * @param thrown 异常对象
     * @param msg 日志消息
     * @param args 消息参数
     * @return 增强的LogRecord实例
     */
    @Override
    public LogRecord createRecord(Level level, Throwable thrown, String msg, Object... args) {
        LogRecord logRecord = Logger.super.createRecord(level, thrown, msg, args);
        if (isNeedToInferCaller()) {
            StackTraceElement stackTraceElement = getStackTraceElement();
            if (stackTraceElement != null) {
                logRecord.setSourceClassName(stackTraceElement.getClassName());
                logRecord.setSourceMethodName(
                        stackTraceElement.getMethodName() + "[" + stackTraceElement.getLineNumber() + "]");
            }
        } else if (isNameToClass()) {
            logRecord.setSourceClassName(logRecord.getLoggerName());
        }
        return logRecord;
    }

    /**
     * 获取调用者堆栈元素（性能敏感操作）。
     * <p>
     * 通过StackTrace获取非日志框架内部的调用者，
     * <b>警告：</b>此操作存在较大性能开销，不建议频繁调用。
     * 
     * @return 调用者堆栈元素，未找到时返回null
     */
    public StackTraceElement getStackTraceElement() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            // 跳过日志框架自身的堆栈帧
            if (FacadeLogger.class.getName().equals(stackTraceElement.getClassName())
                    || Logger.class.getName().equals(stackTraceElement.getClassName())) {
                continue;
            }

            // 跳过Logger接口的实现类堆栈帧
            Class<?> sourceClass = ClassUtils.getClass(stackTraceElement.getClassName(), Logger.class.getClassLoader());
            if (sourceClass != null && Logger.class.isAssignableFrom(sourceClass)) {
                continue;
            }

            return stackTraceElement;
        }
        return null;
    }

    /**
     * 判断指定级别是否可记录。
     * <p>
     * 基于当前级别和CustomLevel的比较逻辑，
     * 级别数值大于等于当前级别时返回true。
     * 
     * @param level 待检查级别
     * @return true表示可记录
     */
    @Override
    public boolean isLoggable(Level level) {
        Level acceptLevel = getLevel();
        if (acceptLevel == null) {
            return true;
        }

        return CustomLevel.isGreaterOrEqual(level, acceptLevel);
    }

    /**
     * 判断日志器名称是否为类名。
     * <p>
     * 通过类加载器检查名称是否对应有效类，
     * 结果会被缓存以避免重复检查。
     * 
     * @return true表示名称是类名
     */
    public boolean isNameToClass() {
        if (isNameToClass == null) {
            synchronized (this) {
                if (isNameToClass == null) {
                    isNameToClass = ClassUtils.findClass(getName(), null).isPresent();
                }
            }
        }
        return isNameToClass;
    }
}