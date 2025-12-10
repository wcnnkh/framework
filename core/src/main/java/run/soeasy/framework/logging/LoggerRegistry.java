package run.soeasy.framework.logging;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 日志器注册表，继承自{@link LevelRegistry}并实现{@link LoggerFactory}接口，
 * 提供日志器的注册、管理和级别配置功能，支持按名称匹配规则批量设置日志级别。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>日志器注册：通过{@link #setLogger(String, Logger)}注册日志器实例</li>
 *   <li>级别管理：继承{@link LevelRegistry}实现按名称匹配的级别配置</li>
 *   <li>线程安全：关键操作使用synchronized保证并发安全</li>
 *   <li>视图访问：通过{@link #getLoggers()}获取所有注册日志器</li>
 * </ul>
 * 
 * <p><b>匹配规则说明：</b>
 * <ul>
 *   <li>名称匹配：支持简单前缀匹配（如"com.example"匹配"com.example.app"）</li>
 *   <li>级别继承：未显式设置级别的日志器继承父级或根级别</li>
 *   <li>批量设置：{@link #setLevel(String, Level)}按名称匹配批量更新</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see LevelRegistry
 * @see LoggerFactory
 * @see FacadeLogger
 */
public class LoggerRegistry extends LevelRegistry implements LoggerFactory {
    /** 日志器映射表（按名称排序），存储注册的日志器实例 */
    private final Map<String, FacadeLogger> loggerMap = new TreeMap<String, FacadeLogger>();

    /**
     * 注册或更新日志器（线程安全操作）。
     * <p>
     * 同步操作确保并发安全，若名称已存在则更新目标日志器，
     * 新注册的日志器会应用已配置的级别（如果有）。
     * 
     * @param name   日志器名称
     * @param logger 日志器实例
     * @return 注册的FacadeLogger包装实例
     */
    public FacadeLogger setLogger(String name, Logger logger) {
        synchronized (this) {
            FacadeLogger dynamicLogger = loggerMap.get(name);
            if (dynamicLogger == null) {
                dynamicLogger = new FacadeLogger(logger);
                Level level = getLevel(name);
                if (level != null) {
                    dynamicLogger.setLevel(level);
                }
                loggerMap.put(name, dynamicLogger);
            } else {
                dynamicLogger.setSource(logger);
            }
            return dynamicLogger;
        }
    }

    /**
     * 获取所有注册的日志器（不可变视图）。
     * <p>
     * 返回Elements包装的日志器集合，支持流式操作，
     * 集合内容与注册表实时同步。
     * 
     * @return 日志器元素集合
     */
    public Streamable<FacadeLogger> getLoggers() {
        return Streamable.of(loggerMap.values());
    }

    /**
     * 设置日志器级别（支持名称匹配）。
     * <p>
     * 同步操作确保一致性，先调用父类{@link LevelRegistry#setLevel(String, Level)}
     * 存储级别配置，再按名称匹配规则更新所有相关日志器。
     * 
     * @param name  日志器名称（支持匹配模式）
     * @param level 目标级别
     */
    @Override
    public void setLevel(@NonNull String name, Level level) {
        synchronized (this) {
            super.setLevel(name, level);
            for (Entry<String, FacadeLogger> entry : loggerMap.entrySet()) {
                if (match(name, entry.getKey())) {
                    entry.getValue().setLevel(level);
                }
            }
        }
    }
    
    /**
     * 根据名称获取日志器。
     * <p>
     * 直接从映射表获取，返回null表示未注册该名称的日志器。
     * 
     * @param name 日志器名称
     * @return 日志器实例，未注册时返回null
     */
    @Override
    public Logger getLogger(String name) {
        return loggerMap.get(name);
    }
}