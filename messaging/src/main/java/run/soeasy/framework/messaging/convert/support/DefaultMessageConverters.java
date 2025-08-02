package run.soeasy.framework.messaging.convert.support;

import run.soeasy.framework.core.spi.Services;
import run.soeasy.framework.messaging.convert.MessageConverters;

/**
 * 默认消息转换器集合，继承自{@link MessageConverters}，提供框架默认的转换器配置，
 * 核心逻辑是将系统级转换器集合（{@link MessageConverters#system()}）作为后备转换器，
 * 确保在没有自定义转换器匹配时，使用系统预配置的基础转换器（文本、字节数组、表单查询字符串等）。
 * 
 * <p>设计意图：
 * - 作为框架默认的转换器集合实现，简化用户配置，无需手动注册基础转换器；
 * - 通过{@link Services#setLast(Object)}将系统级转换器添加到末尾，保证自定义转换器优先于系统默认转换器执行，
 *   同时保留系统转换器作为最终后备（避免因缺少转换器导致转换失败）。
 * 
 * @author soeasy.run
 * @see MessageConverters
 * @see MessageConverters#system()
 */
public class DefaultMessageConverters extends MessageConverters {

    /**
     * 初始化默认消息转换器集合，将系统级转换器集合设置为最后的后备转换器
     * 
     * <p>通过{@link Services#setLast(Object)}将{@link MessageConverters#system()}添加到当前集合末尾，
     * 使得：
     * 1. 自定义注册的转换器（通过{@link Services#register(Object)}）优先匹配；
     * 2. 当无自定义转换器匹配时，自动使用系统级转换器（文本、字节数组、表单查询字符串等基础转换）。
     */
    public DefaultMessageConverters() {
        setLast(system());
    }
}