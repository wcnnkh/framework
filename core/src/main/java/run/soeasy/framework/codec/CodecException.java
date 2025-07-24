package run.soeasy.framework.codec;

/**
 * 编解码过程中发生的异常的基类，继承自{@link RuntimeException}，
 * 用于表示在编解码操作（如加密、解密、序列化、反序列化）过程中出现的错误。
 * 
 * <p><b>异常分类：</b>
 * <ul>
 *   <li>{@link EncodeException}：编码过程中发生的异常</li>
 *   <li>{@link DecodeException}：解码过程中发生的异常</li>
 * </ul>
 * 
 * <p><b>使用建议：</b>
 * <ul>
 *   <li>捕获特定子类异常（EncodeException/DecodeException）进行针对性处理</li>
 *   <li>通过{@link #getCause()}获取原始异常，进行更精确的错误诊断</li>
 *   <li>建议提供包含错误信息和原始异常的构造函数调用</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see EncodeException
 * @see DecodeException
 */
public class CodecException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建带有指定错误消息的编解码异常。
     * 
     * @param msg 错误消息，可为null
     */
    public CodecException(String msg) {
        super(msg);
    }

    /**
     * 创建由其他异常引起的编解码异常。
     * 
     * @param cause 原始异常，可为null
     */
    public CodecException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建带有指定错误消息和原始异常的编解码异常。
     * 
     * @param message 错误消息，可为null
     * @param cause   原始异常，可为null
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}