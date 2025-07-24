package run.soeasy.framework.core.collection;

/**
 * 非唯一元素异常，用于表示期望唯一元素但实际存在多个或不存在的场景。
 * 该异常继承自RuntimeException，属于运行时异常，调用方无需强制捕获。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>集合要求元素唯一但发现重复元素时</li>
 *   <li>查询期望唯一结果但返回多个结果时</li>
 *   <li>键值对映射要求键唯一但存在重复键时</li>
 * </ul>
 *
 * @see RuntimeException
 */
public class NoUniqueElementException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 创建非唯一元素异常实例，无错误消息。
     */
    public NoUniqueElementException() {
        super();
    }

    /**
     * 创建非唯一元素异常实例，附带指定错误消息。
     *
     * @param message 描述异常原因的错误消息
     */
    public NoUniqueElementException(String message) {
        super(message);
    }
}