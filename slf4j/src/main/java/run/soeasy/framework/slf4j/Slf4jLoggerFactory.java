package run.soeasy.framework.slf4j;

import org.slf4j.LoggerFactory;

import run.soeasy.framework.logging.Logger;

/**
 * SLF4J日志工厂实现，实现自定义的{@link run.soeasy.framework.logging.LoggerFactory}接口，
 * 用于创建适配SLF4J框架的日志器（{@link Slf4jLogger}），负责将日志名称与SLF4J的日志体系关联，
 * 使应用可通过统一的日志工厂接口获取基于SLF4J的日志实例。
 * 
 * <p>该工厂通过SLF4J的{@link LoggerFactory}获取底层日志器，封装为自定义的{@link Slf4jLogger}，
 * 实现日志接口的适配与桥接，便于日志框架的统一管理与切换。
 * 
 * @author soeasy.run
 * @see run.soeasy.framework.logging.LoggerFactory
 * @see Slf4jLogger
 * @see org.slf4j.LoggerFactory
 */
public class Slf4jLoggerFactory implements run.soeasy.framework.logging.LoggerFactory {

    /**
     * 根据指定名称获取适配SLF4J的日志器
     * 
     * <p>通过SLF4J的{@link LoggerFactory#getLogger(String)}获取对应名称的日志器，
     * 并包装为{@link Slf4jLogger}实例返回，此处消息占位符暂设为null（可根据需求扩展为默认占位符）。
     * 
     * @param name 日志器名称（通常为类全限定名，用于标识日志来源）
     * @return 适配SLF4J的{@link Slf4jLogger}实例
     */
    @Override
    public Logger getLogger(String name) {
        // 通过SLF4J获取指定名称的底层日志器
        org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(name);
        // 创建并返回SLF4J日志适配器
        return new Slf4jLogger(slf4jLogger);
    }
}