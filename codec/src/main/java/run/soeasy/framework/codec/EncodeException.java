package run.soeasy.framework.codec;

/**
 * 编码过程中发生的异常，继承自{@link CodecException}，
 * 用于表示在执行编码操作（如加密、序列化、格式转换）过程中出现的错误。
 * 
 * <p><b>常见场景：</b>
 * <ul>
 *   <li>数据格式不支持：输入数据类型或结构不符合编码器要求</li>
 *   <li>参数错误：编码器所需的配置参数缺失或无效</li>
 *   <li>资源不足：编码过程中所需的系统资源不足（如内存、文件句柄）</li>
 *   <li>外部依赖失败：依赖的外部系统或服务调用失败</li>
 * </ul>
 * 
 * <p><b>处理建议：</b>
 * <ul>
 *   <li>捕获此异常进行编码失败的针对性处理</li>
 *   <li>通过{@link #getCause()}获取原始异常，进行更精确的错误诊断</li>
 *   <li>验证输入数据的有效性和完整性</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see CodecException
 * @see DecodeException
 */
public class EncodeException extends CodecException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建带有指定错误消息的编码异常。
     * 
     * @param msg 错误消息，可为null
     */
    public EncodeException(String msg) {
        super(msg);
    }

    /**
     * 创建由其他异常引起的编码异常。
     * 
     * @param cause 原始异常，可为null
     */
    public EncodeException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建带有指定错误消息和原始异常的编码异常。
     * 
     * @param message 错误消息，可为null
     * @param cause   原始异常，可为null
     */
    public EncodeException(String message, Throwable cause) {
        super(message, cause);
    }
}