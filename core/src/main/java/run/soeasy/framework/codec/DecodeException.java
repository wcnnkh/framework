package run.soeasy.framework.codec;

/**
 * 解码过程中发生的异常，继承自{@link CodecException}，
 * 用于表示在执行解码操作（如解密、反序列化、格式解析）过程中出现的错误。
 * 
 * <p><b>常见场景：</b>
 * <ul>
 *   <li>数据格式错误：输入数据不符合预期的编码格式</li>
 *   <li>数据损坏：输入数据在传输或存储过程中发生损坏</li>
 *   <li>版本不兼容：输入数据的编码版本与当前解码器不兼容</li>
 *   <li>密钥错误：解密过程中使用了错误的密钥</li>
 * </ul>
 * 
 * <p><b>处理建议：</b>
 * <ul>
 *   <li>捕获此异常进行解码失败的针对性处理</li>
 *   <li>通过{@link #getCause()}获取原始异常，进行更精确的错误诊断</li>
 *   <li>考虑记录详细的输入数据信息（如部分内容、长度等）辅助调试</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see CodecException
 * @see EncodeException
 */
public class DecodeException extends CodecException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建带有指定错误消息的解码异常。
     * 
     * @param msg 错误消息，可为null
     */
    public DecodeException(String msg) {
        super(msg);
    }

    /**
     * 创建由其他异常引起的解码异常。
     * 
     * @param cause 原始异常，可为null
     */
    public DecodeException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建带有指定错误消息和原始异常的解码异常。
     * 
     * @param message 错误消息，可为null
     * @param cause   原始异常，可为null
     */
    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}