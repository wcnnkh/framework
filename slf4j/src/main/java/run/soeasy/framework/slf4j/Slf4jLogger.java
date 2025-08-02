package run.soeasy.framework.slf4j;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.Logger;

import run.soeasy.framework.logging.CustomLevel;
import run.soeasy.framework.logging.FormatableMessage;

/**
 * SLF4J日志适配器，实现自定义的{@link run.soeasy.framework.logging.Logger}接口，
 * 用于将Java Logging API的日志记录（{@link LogRecord}）适配到SLF4J日志框架，
 * 仅支持常规日志级别（info、debug、trace、warn、error）的转换与处理。
 * 
 * <p>该适配器通过包装SLF4J的{@link Logger}，实现日志级别的映射、消息格式化及异常传递，
 * 使基于Java Logging API的代码可无缝集成SLF4J及其底层日志实现（如Logback、Log4j2等）。
 * 
 * @author soeasy.run
 * @see Logger
 * @see Logger
 * @see LogRecord
 */
public class Slf4jLogger implements run.soeasy.framework.logging.Logger {

    /**
     * SLF4J日志消息的占位符格式（固定为"{}"，符合SLF4J的参数占位规范）
     */
    private static final String FORMAT = "{}";

    /**
     * 被包装的SLF4J日志器，实际执行日志输出
     */
    private final Logger logger;

    /**
     * 消息格式化时使用的参数占位符（用于解析{@link LogRecord}中的参数）
     */
    private final String placeholder;
    
    public Slf4jLogger(Logger logger) {
    	this(logger, FORMAT);
    }

    /**
     * 构造SLF4J日志适配器
     * 
     * @param logger SLF4J日志器实例（实际执行日志输出）
     * @param placeholder 消息中参数的占位符（如"{0}"，用于{@link FormatableMessage}解析参数）
     */
    public Slf4jLogger(Logger logger, String placeholder) {
        this.logger = logger;
        this.placeholder = placeholder;
    }

    /**
     * 获取日志器名称（与SLF4J日志器名称一致）
     * 
     * @return 日志器名称（通常为类全限定名）
     */
    @Override
    public String getName() {
        return logger.getName();
    }

    /**
     * 判断指定日志级别是否可输出（适配SLF4J的日志级别开关）
     * 
     * <p>映射关系：
     * <ul>
     * <li>{@link Level#INFO} → SLF4J info级别开关</li>
     * <li>{@link CustomLevel#DEBUG} → SLF4J debug级别开关</li>
     * <li>{@link CustomLevel#TRACE} → SLF4J trace级别开关</li>
     * <li>{@link CustomLevel#WARN} → SLF4J warn级别开关</li>
     * <li>{@link CustomLevel#ERROR} → SLF4J error级别开关</li>
     * </ul>
     * 未支持的级别默认返回true（允许输出）。
     * 
     * @param level 日志级别（Java Logging API的级别）
     * @return 该级别是否允许输出（true为允许）
     */
    @Override
    public boolean isLoggable(Level level) {
        String levelName = level.getName();
        if (levelName.equalsIgnoreCase(Level.INFO.getName())) {
            return logger.isInfoEnabled();
        } else if (levelName.equalsIgnoreCase(CustomLevel.DEBUG.getName())) {
            return logger.isDebugEnabled();
        } else if (levelName.equalsIgnoreCase(CustomLevel.TRACE.getName())) {
            return logger.isTraceEnabled();
        } else if (levelName.equalsIgnoreCase(CustomLevel.WARN.getName())) {
            return logger.isWarnEnabled();
        } else if (levelName.equalsIgnoreCase(CustomLevel.ERROR.getName())) {
            return logger.isErrorEnabled();
        } else {
            // 未支持的级别默认允许输出
            return true;
        }
    }

    /**
     * 处理日志记录，转换为SLF4J日志输出
     * 
     * <p>处理流程：
     * 1. 使用{@link FormatableMessage}格式化日志消息（结合占位符和参数）；
     * 2. 提取日志记录中的异常（{@link LogRecord#getThrown()}）；
     * 3. 根据日志级别调用SLF4J日志器对应的方法（如info、debug），并传递格式化消息和异常。
     * 
     * @param record Java Logging的日志记录（包含级别、消息、参数、异常等）
     */
    @Override
    public void log(LogRecord record) {
        // 格式化日志消息（替换占位符为实际参数）
        FormatableMessage message = new FormatableMessage(record.getMessage(), placeholder, record.getParameters());
        Throwable thrown = record.getThrown();
        String levelName = record.getLevel().getName();

        // 根据日志级别调用对应的SLF4J日志方法
        if (levelName.equalsIgnoreCase(Level.INFO.getName())) {
            logInfo(message, thrown);
        } else if (levelName.equalsIgnoreCase(CustomLevel.DEBUG.getName())) {
            logDebug(message, thrown);
        } else if (levelName.equalsIgnoreCase(CustomLevel.TRACE.getName())) {
            logTrace(message, thrown);
        } else if (levelName.equalsIgnoreCase(CustomLevel.WARN.getName())) {
            logWarn(message, thrown);
        } else if (levelName.equalsIgnoreCase(CustomLevel.ERROR.getName())) {
            logError(message, thrown);
        } else {
            // 处理未支持的日志级别（默认用info输出并标记未支持）
            logUnsupportedLevel(record, message, thrown);
        }
    }

    /**
     * 输出INFO级日志
     */
    private void logInfo(FormatableMessage message, Throwable thrown) {
        if (thrown == null) {
            logger.info(FORMAT, message);
        } else {
            logger.info(message.toString(), thrown);
        }
    }

    /**
     * 输出DEBUG级日志
     */
    private void logDebug(FormatableMessage message, Throwable thrown) {
        if (thrown == null) {
            logger.debug(FORMAT, message);
        } else {
            logger.debug(message.toString(), thrown);
        }
    }

    /**
     * 输出TRACE级日志
     */
    private void logTrace(FormatableMessage message, Throwable thrown) {
        if (thrown == null) {
            logger.trace(FORMAT, message);
        } else {
            logger.trace(message.toString(), thrown);
        }
    }

    /**
     * 输出WARN级日志
     */
    private void logWarn(FormatableMessage message, Throwable thrown) {
        if (thrown == null) {
            logger.warn(FORMAT, message);
        } else {
            logger.warn(message.toString(), thrown);
        }
    }

    /**
     * 输出ERROR级日志
     */
    private void logError(FormatableMessage message, Throwable thrown) {
        if (thrown == null) {
            logger.error(FORMAT, message);
        } else {
            logger.error(message.toString(), thrown);
        }
    }

    /**
     * 处理未支持的日志级别（默认用INFO级输出并提示）
     */
    private void logUnsupportedLevel(LogRecord record, FormatableMessage message, Throwable thrown) {
        String logMsg = "Unsupported " + record.getLevel() + " | " + message;
        if (thrown == null) {
            logger.info(logMsg);
        } else {
            logger.info(logMsg, thrown);
        }
    }
}