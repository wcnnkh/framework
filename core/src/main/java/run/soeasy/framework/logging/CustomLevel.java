package run.soeasy.framework.logging;

import java.util.logging.Level;

import lombok.NonNull;

/**
 * 自定义日志级别类，扩展自Java标准{@link Level}类，
 * 提供更清晰的日志级别定义和比较功能。
 * 
 * <p>该类定义了以下自定义日志级别：
 * <ul>
 *   <li>{@link #TRACE}：比FINER更详细的跟踪日志（数值：{@value Level#FINER#intValue()}）</li>
 *   <li>{@link #DEBUG}：调试日志（数值：{@value Level#FINE#intValue()}）</li>
 *   <li>{@link #WARN}：警告日志（数值：{@value Level#WARNING#intValue()}）</li>
 *   <li>{@link #ERROR}：错误日志（数值：{@value Level#SEVERE#intValue()}）</li>
 * </ul>
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>兼容标准级别：数值与Java标准级别兼容，可无缝替换</li>
 *   <li>清晰的级别命名：使用更直观的名称（如WARN替代WARNING）</li>
 *   <li>便捷的级别比较：提供{@link #isGreaterOrEqual(Level, Level)}方法</li>
 *   <li>字符串解析：支持通过{@link #parse(String)}解析级别名称</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Level
 * @see java.util.logging.Logger
 */
public class CustomLevel extends Level {
    private static final long serialVersionUID = 1L;
    
    /** 跟踪日志级别（比FINER更详细） */
    public static final CustomLevel TRACE = new CustomLevel("TRACE", Level.FINER.intValue(),
            Level.FINE.getResourceBundleName());
    
    /** 调试日志级别（对应FINE） */
    public static final CustomLevel DEBUG = new CustomLevel("DEBUG", FINE.intValue(),
            Level.CONFIG.getResourceBundleName());
    
    /** 警告日志级别（对应WARNING） */
    public static final CustomLevel WARN = new CustomLevel("WARN", Level.WARNING.intValue(),
            Level.WARNING.getResourceBundleName());
    
    /** 错误日志级别（对应SEVERE） */
    public static final CustomLevel ERROR = new CustomLevel("ERROR", Level.SEVERE.intValue(),
            Level.SEVERE.getResourceBundleName());

    /**
     * 构造自定义日志级别。
     * 
     * @param name  级别名称（如"TRACE"）
     * @param value 级别数值（需与标准级别兼容）
     */
    public CustomLevel(String name, int value) {
        super(name, value);
    }

    /**
     * 构造带资源绑定的自定义日志级别。
     * 
     * @param name              级别名称
     * @param value             级别数值
     * @param resourceBundleName 资源绑定名称
     */
    public CustomLevel(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }

    /**
     * 判断源级别是否大于或等于目标级别。
     * 
     * @param origin 源级别
     * @param target 目标级别
     * @return true表示源级别数值≥目标级别数值
     */
    public static boolean isGreaterOrEqual(Level origin, Level target) {
        return origin.intValue() >= target.intValue();
    }

    /**
     * 解析字符串为日志级别（忽略大小写）。
     * 
     * @param levelName 级别名称（如"debug"、"WARN"）
     * @return 对应的Level实例
     * @throws IllegalArgumentException 当名称无法解析时抛出
     */
    public static Level parse(@NonNull String levelName) {
        return Level.parse(levelName.toUpperCase());
    }
}