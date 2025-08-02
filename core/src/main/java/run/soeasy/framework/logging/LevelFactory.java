package run.soeasy.framework.logging;

import java.util.logging.Level;

import lombok.NonNull;

/**
 * 日志级别工厂接口，定义获取日志级别的标准方法，
 * 支持根据日志器名称动态获取对应的日志级别配置。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>级别获取：根据名称获取对应的日志级别</li>
 *   <li>动态配置：支持运行时根据名称规则动态确定级别</li>
 *   <li>层级管理：可能支持级别继承或默认级别处理</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志配置：根据类名或模块名获取预配置的日志级别</li>
 *   <li>动态调整：运行时根据条件动态设置不同组件的日志级别</li>
 *   <li>级别管理：统一管理项目中各组件的日志级别配置</li>
 * </ul>
 *
 * @author soeasy.run
 * @see CustomLevel
 * @see LevelRegistry
 */
public interface LevelFactory {
    /**
     * 根据名称获取对应的日志级别。
     * <p>
     * 实现类应根据名称规则（如类名、模块名）返回对应的日志级别，
     * 支持精确匹配或规则匹配，并可返回默认级别。
     * 
     * @param name 日志器名称（如类全限定名或模块名），不可为null
     * @return 匹配的日志级别，不可为null（需保证默认级别存在）
     */
    Level getLevel(@NonNull String name);
}