package run.soeasy.framework.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import run.soeasy.framework.core.ObjectUtils;

/**
 * 日志记录接口，定义统一的日志操作规范，支持不同级别日志记录、参数化消息和异常处理。
 * 该接口封装了Java标准日志API，提供更简洁的日志记录方式和自定义日志级别的支持。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>多级日志支持：提供TRACE/DEBUG/INFO/WARN/ERROR五个日志级别</li>
 *   <li>参数化消息：支持占位符消息格式化（如{@code log("操作{0}失败", "文件")}）</li>
 *   <li>异常封装：支持将Throwable与日志消息关联记录</li>
 *   <li>级别检查：提供{@code isDebugEnabled()}等方法判断级别是否启用</li>
 *   <li>标准兼容：基于Java标准{@link LogRecord}实现，可无缝对接现有日志系统</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>应用程序日志记录：记录业务流程、操作轨迹和异常信息</li>
 *   <li>框架日志集成：作为统一日志接口适配不同日志实现</li>
 *   <li>日志级别控制：运行时动态调整日志输出级别</li>
 *   <li>参数化日志：避免不必要的字符串拼接，提高性能</li>
 * </ul>
 *
 * @author soeasy.run
 * @see LogRecord
 * @see CustomLevel
 */
public interface Logger {

    /**
     * 创建日志记录对象。
     * <p>
     * 封装日志级别、消息、参数和异常信息到{@link LogRecord}，
     * 自动设置日志器名称（通过{@link #getName()}获取）。
     * 
     * @param level  日志级别，不可为null
     * @param thrown 异常对象（可选）
     * @param msg    日志消息（支持占位符）
     * @param args   消息参数（与占位符对应）
     * @return 初始化后的LogRecord实例
     */
    default LogRecord createRecord(Level level, Throwable thrown, String msg, Object... args) {
        LogRecord logRecord = new LogRecord(level, msg);
        if (thrown != null) {
            logRecord.setThrown(thrown);
        }

        if (args != null) {
            logRecord.setParameters(args);
        }

        logRecord.setLoggerName(getName());
        return logRecord;
    }

    /**
     * 记录DEBUG级别日志（消息无参数）。
     * <p>
     * 等价于调用{@code log(createRecord(CustomLevel.DEBUG, null, msg, EMPTY_ARRAY))}。
     * 
     * @param msg 日志消息
     */
    default void debug(String msg) {
        log(createRecord(CustomLevel.DEBUG, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录DEBUG级别日志（带参数化消息）。
     * 
     * @param msg  日志消息（支持占位符如"操作{0}成功"）
     * @param args 替换参数（如new Object[]{"文件"}）
     */
    default void debug(String msg, Object... args) {
        log(createRecord(CustomLevel.DEBUG, null, msg, args));
    }

    /**
     * 记录DEBUG级别日志（带异常和消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     */
    default void debug(Throwable e, String msg) {
        log(createRecord(CustomLevel.DEBUG, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录DEBUG级别日志（带异常和参数化消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void debug(Throwable e, String msg, Object... args) {
        log(createRecord(CustomLevel.DEBUG, e, msg, args));
    }

    /**
     * 记录ERROR级别日志（消息无参数）。
     * 
     * @param msg 日志消息
     */
    default void error(String msg) {
        log(createRecord(CustomLevel.ERROR, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录ERROR级别日志（带参数化消息）。
     * 
     * @param msg  日志消息
     * @param args 替换参数
     */
    default void error(String msg, Object... args) {
        log(createRecord(CustomLevel.ERROR, null, msg, args));
    }

    /**
     * 记录ERROR级别日志（带异常和消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     */
    default void error(Throwable e, String msg) {
        log(createRecord(CustomLevel.ERROR, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录ERROR级别日志（带异常和参数化消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void error(Throwable e, String msg, Object... args) {
        log(createRecord(CustomLevel.ERROR, e, msg, args));
    }

    /**
     * 获取日志器名称。
     * 
     * @return 日志器名称（如类名或配置的名称）
     */
    String getName();

    /**
     * 记录INFO级别日志（消息无参数）。
     * 
     * @param msg 日志消息
     */
    default void info(String msg) {
        log(createRecord(CustomLevel.INFO, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录INFO级别日志（带参数化消息）。
     * 
     * @param msg  日志消息
     * @param args 替换参数
     */
    default void info(String msg, Object... args) {
        log(createRecord(CustomLevel.INFO, null, msg, args));
    }

    /**
     * 记录INFO级别日志（带异常和消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     */
    default void info(Throwable e, String msg) {
        log(createRecord(CustomLevel.INFO, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录INFO级别日志（带异常和参数化消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void info(Throwable e, String msg, Object... args) {
        log(createRecord(CustomLevel.INFO, e, msg, args));
    }

    /**
     * 判断DEBUG级别是否启用。
     * <p>
     * 等价于调用{@code isLoggable(CustomLevel.DEBUG)}。
     * 
     * @return true表示DEBUG级别已启用
     */
    default boolean isDebugEnabled() {
        return isLoggable(CustomLevel.DEBUG);
    }

    /**
     * 判断ERROR级别是否启用。
     * 
     * @return true表示ERROR级别已启用
     */
    default boolean isErrorEnabled() {
        return isLoggable(CustomLevel.ERROR);
    }

    /**
     * 判断INFO级别是否启用。
     * 
     * @return true表示INFO级别已启用
     */
    default boolean isInfoEnabled() {
        return isLoggable(CustomLevel.INFO);
    }

    /**
     * 判断指定日志级别是否启用。
     * <p>
     * 由实现类决定如何判断级别是否启用（如检查配置或级别阈值）。
     * 
     * @param level 待检查的日志级别
     * @return true表示级别已启用
     */
    boolean isLoggable(Level level);

    /**
     * 判断TRACE级别是否启用。
     * 
     * @return true表示TRACE级别已启用
     */
    default boolean isTraceEnabled() {
        return isLoggable(CustomLevel.TRACE);
    }

    /**
     * 判断WARN级别是否启用。
     * 
     * @return true表示WARN级别已启用
     */
    default boolean isWarnEnabled() {
        return isLoggable(CustomLevel.WARN);
    }

    /**
     * 记录指定级别日志（消息无参数）。
     * 
     * @param level 日志级别
     * @param msg 日志消息
     */
    default void log(Level level, String msg) {
        log(createRecord(level, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录指定级别日志（带参数化消息）。
     * 
     * @param level 日志级别
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void log(Level level, String msg, Object... args) {
        log(createRecord(level, null, msg, args));
    }

    /**
     * 记录指定级别日志（带异常和消息）。
     * 
     * @param level 日志级别
     * @param e 异常对象
     * @param msg 日志消息
     */
    default void log(Level level, Throwable e, String msg) {
        log(createRecord(level, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录指定级别日志（带异常和参数化消息）。
     * 
     * @param level 日志级别
     * @param e 异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void log(Level level, Throwable e, String msg, Object... args) {
        log(createRecord(level, e, msg, args));
    }

    /**
     * 记录日志记录对象。
     * <p>
     * 由实现类决定如何处理{@link LogRecord}，
     * 是日志记录的最终执行方法。
     * 
     * @param record 日志记录对象，不可为null
     */
    void log(LogRecord record);

    /**
     * 记录TRACE级别日志（消息无参数）。
     * 
     * @param msg 日志消息
     */
    default void trace(String msg) {
        log(createRecord(CustomLevel.TRACE, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录TRACE级别日志（带参数化消息）。
     * 
     * @param msg  日志消息
     * @param args 替换参数
     */
    default void trace(String msg, Object... args) {
        log(createRecord(CustomLevel.TRACE, null, msg, args));
    }

    /**
     * 记录TRACE级别日志（带异常和消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     */
    default void trace(Throwable e, String msg) {
        log(createRecord(CustomLevel.TRACE, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录TRACE级别日志（带异常和参数化消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void trace(Throwable e, String msg, Object... args) {
        log(createRecord(CustomLevel.TRACE, e, msg, args));
    }

    /**
     * 记录WARN级别日志（消息无参数）。
     * 
     * @param msg 日志消息
     */
    default void warn(String msg) {
        log(createRecord(CustomLevel.WARN, null, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录WARN级别日志（带参数化消息）。
     * 
     * @param msg  日志消息
     * @param args 替换参数
     */
    default void warn(String msg, Object... args) {
        log(createRecord(CustomLevel.WARN, null, msg, args));
    }

    /**
     * 记录WARN级别日志（带异常和消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     */
    default void warn(Throwable e, String msg) {
        log(createRecord(CustomLevel.WARN, e, msg, ObjectUtils.EMPTY_ARRAY));
    }

    /**
     * 记录WARN级别日志（带异常和参数化消息）。
     * 
     * @param e   异常对象
     * @param msg 日志消息
     * @param args 替换参数
     */
    default void warn(Throwable e, String msg, Object... args) {
        log(createRecord(CustomLevel.WARN, e, msg, args));
    }
}