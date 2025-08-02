package run.soeasy.framework.messaging;

import java.nio.charset.Charset;

import run.soeasy.framework.core.domain.CharsetCapableWrapper;

/**
 * 消息包装器接口，继承自{@link Message}和{@link CharsetCapableWrapper}，
 * 采用委托模式对{@link Message}实例进行包装，通过复用源消息的实现简化包装类的开发，
 * 同时保持{@link Message}接口的完整契约，支持在不修改原始消息的前提下扩展功能。
 * 
 * <p>作为函数式接口，其核心抽象方法为{@link CharsetCapableWrapper#getSource()}，
 * 用于获取被包装的原始消息对象，所有默认方法均通过该方法委托给源消息的对应实现。
 * 
 * @param <W> 被包装的消息类型（需继承自{@link Message}）
 * @author soeasy.run
 * @see Message
 * @see CharsetCapableWrapper
 */
@FunctionalInterface
public interface MessageWrapper<W extends Message> extends Message, CharsetCapableWrapper<W> {

    /**
     * 获取消息字符集（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的字符集，等同于{@code getSource().getCharset()}。
     * 
     * @return 字符集对象（可能为null，由源消息决定）
     */
    @Override
    default Charset getCharset() {
        return getSource().getCharset();
    }

    /**
     * 获取消息字符集名称（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的字符集名称，等同于{@code getSource().getCharsetName()}。
     * 
     * @return 字符集名称（可能为null，由源消息决定）
     */
    @Override
    default String getCharsetName() {
        return getSource().getCharsetName();
    }

    /**
     * 获取消息头部信息（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的头部对象，等同于{@code getSource().getHeaders()}。
     * 
     * @return 消息头部对象（非空，由源消息提供）
     */
    @Override
    default Headers getHeaders() {
        return getSource().getHeaders();
    }

    /**
     * 获取消息内容类型（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的内容类型，等同于{@code getSource().getContentType()}。
     * 
     * @return 内容类型（可能为null，由源消息决定）
     */
    @Override
    default MediaType getContentType() {
        return getSource().getContentType();
    }

    /**
     * 获取消息内容长度（委托给源消息）
     * 
     * <p>默认实现：返回被包装消息的内容长度，等同于{@code getSource().getContentLength()}，
     * 修正原实现中的递归调用问题，确保正确委托给源消息。
     * 
     * @return 消息内容长度（字节数，由源消息提供）
     */
    @Override
    default long getContentLength() {
        return getSource().getContentLength();
    }
}