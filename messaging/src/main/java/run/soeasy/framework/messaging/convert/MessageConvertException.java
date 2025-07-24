package run.soeasy.framework.messaging.convert;

/**
 * 消息转换异常，继承自{@link RuntimeException}，用于标识消息在序列化或反序列化过程中发生的错误，
 * 涵盖消息读取（{@link MessageReader}）和消息写入（{@link MessageWriter}）过程中的转换失败场景。
 * 
 * <p>该异常通常包装底层异常（如I/O错误、格式解析错误、类型转换错误等），
 * 提供消息转换过程的错误上下文，便于问题排查。
 * 
 * @author soeasy.run
 * @see MessageReader
 * @see MessageWriter
 */
public class MessageConvertException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 构造一个包含底层原因的消息转换异常
     * 
     * @param cause 导致转换失败的底层异常（非空）
     */
    public MessageConvertException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个包含错误消息的消息转换异常
     * 
     * @param message 描述转换失败的详细消息（非空）
     */
    public MessageConvertException(String message) {
        super(message);
    }

    /**
     * 构造一个包含错误消息和底层原因的消息转换异常
     * 
     * @param message 描述转换失败的详细消息（非空）
     * @param cause 导致转换失败的底层异常（非空）
     */
    public MessageConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}