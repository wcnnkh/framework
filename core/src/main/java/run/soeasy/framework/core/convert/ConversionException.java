package run.soeasy.framework.core.convert;

/**
 * 类型转换过程中发生的异常的基类。
 * 当在类型转换过程中发生错误时，抛出此异常或其派生类。
 *
 * <p>该异常提供三种构造函数：
 * <ul>
 *   <li>仅带消息的构造函数</li>
 *   <li>仅带原因的构造函数</li>
 *   <li>同时带消息和原因的构造函数</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * try {
 *     Object result = converter.convert(source, sourceType, targetType);
 * } catch (ConversionException ex) {
 *     // 处理转换异常
 *     log.error("Type conversion failed", ex);
 * }
 * }</pre>
 *
 * @author soeasy.run
 * @see Converter
 */
public class ConversionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建一个新的转换异常，包含指定的错误消息。
     * 
     * @param message 描述异常原因的消息
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * 创建一个新的转换异常，由指定的原因引起。
     * 
     * @param cause 导致此异常的根本原因
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建一个新的转换异常，包含指定的错误消息和原因。
     * 
     * @param message 描述异常原因的消息
     * @param cause 导致此异常的根本原因
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}