package run.soeasy.framework.dom;

/**
 * DocumentException 是一个运行时异常，用于表示在处理 DOM 文档过程中发生的错误。
 * 该异常封装了在解析、转换或操作 DOM 文档时可能出现的各种问题，
 * 允许框架以统一的方式处理与文档相关的异常情况。
 * 
 * <p>该异常类提供了多个构造函数，支持携带错误消息、原始异常原因，
 * 以便在异常传播过程中保留完整的错误上下文信息。
 * 
 * @see RuntimeException
 */
public class DocumentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 使用指定的错误消息构造一个新的 DocumentException。
     * 
     * @param message 详细描述错误的消息
     */
    public DocumentException(String message) {
        super(message);
    }

    /**
     * 使用指定的原始异常原因构造一个新的 DocumentException。
     * 
     * @param cause 导致此异常的原始异常
     */
    public DocumentException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用指定的错误消息和原始异常原因构造一个新的 DocumentException。
     * 
     * @param message 详细描述错误的消息
     * @param cause 导致此异常的原始异常
     */
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}