package run.soeasy.framework.messaging.channel;

import java.io.IOException;

import run.soeasy.framework.messaging.InputMessage;

/**
 * 消息通道处理器接口，定义了消息通道生命周期及消息处理的回调方法，
 * 用于响应{@link MessageChannel}的各种事件（如通道打开、接收消息、发生错误、通道关闭），
 * 是处理消息通道事件的核心接口。
 * 
 * <p>通过实现该接口，可以自定义消息通道的事件处理逻辑，例如：
 * - 通道打开时初始化资源；
 * - 接收消息时进行业务处理；
 * - 发生错误时记录日志或进行恢复；
 * - 通道关闭时释放资源。
 * 
 * @author soeasy.run
 * @see MessageChannel
 * @see InputMessage
 */
public interface MessageChannelHandler {

    /**
     * 通道打开事件的回调方法
     * 
     * <p>当消息通道成功打开并准备好进行消息交换时调用，可在此方法中执行初始化操作。
     * 
     * @param channel 已打开的消息通道（非空）
     * @throws IOException 若处理过程中发生I/O错误
     */
    void onOpen(MessageChannel channel) throws IOException;

    /**
     * 接收消息事件的回调方法
     * 
     * <p>当消息通道接收到新的输入消息时调用，可在此方法中处理消息内容（如解析、验证、业务逻辑执行）。
     * 
     * @param channel 接收消息的通道（非空）
     * @param message 接收到的输入消息（非空）
     * @throws IOException 若处理过程中发生I/O错误（如消息读取失败）
     */
    void onMessage(MessageChannel channel, InputMessage message) throws IOException;

    /**
     * 错误事件的回调方法
     * 
     * <p>当消息通道发生异常时调用，可在此方法中记录错误日志、执行错误恢复或通知上层组件。
     * 
     * @param channel 发生错误的通道（非空）
     * @param throwable 发生的异常（非空）
     * @throws IOException 若错误处理过程中发生I/O错误
     */
    void onError(MessageChannel channel, Throwable throwable) throws IOException;

    /**
     * 通道关闭事件的回调方法
     * 
     * <p>当消息通道关闭时调用，可在此方法中释放与通道关联的资源（如输入流、网络连接等）。
     * 
     * @param channel 已关闭的通道（非空）
     * @throws IOException 若处理过程中发生I/O错误
     */
    void onClose(MessageChannel channel) throws IOException;
}