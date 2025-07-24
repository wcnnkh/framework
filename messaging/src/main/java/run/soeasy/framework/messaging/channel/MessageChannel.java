package run.soeasy.framework.messaging.channel;

import java.io.Closeable;

import run.soeasy.framework.core.exchange.Channel;
import run.soeasy.framework.messaging.InputMessage;

/**
 * 消息通道接口，继承自{@link Channel}和{@link Closeable}，
 * 定义了处理{@link InputMessage}的通道规范，支持消息交换和资源关闭操作，
 * 是消息传递系统中连接消息生产者和消费者的核心组件。
 * 
 * <p>核心功能：
 * - 作为{@link Channel}的子接口，支持输入消息（{@link InputMessage}）的交换；
 * - 继承{@link Closeable}接口，提供资源释放能力，确保通道关闭时释放相关资源；
 * - 提供通道关闭状态的检查方法（{@link #isClosed()}），避免对已关闭通道进行操作。
 * 
 * @author soeasy.run
 * @see Channel
 * @see InputMessage
 * @see Closeable
 */
public interface MessageChannel extends Channel<InputMessage>, Closeable {

    /**
     * 判断当前消息通道是否已关闭
     * 
     * <p>通道关闭后，通常无法再进行消息交换操作，调用相关方法可能会抛出异常。
     * 
     * @return 已关闭返回true，否则返回false
     */
    boolean isClosed();
}