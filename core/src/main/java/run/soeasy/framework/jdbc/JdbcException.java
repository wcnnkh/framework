package run.soeasy.framework.jdbc;

/**
 * JDBC操作相关的运行时异常类，继承自{@link RuntimeException}，
 * 用于封装JDBC操作过程中发生的各种错误（如数据库连接失败、SQL执行异常、参数绑定错误等），
 * 作为非检查型异常，允许在JDBC操作中无需强制捕获，适合向上层传递数据库交互错误。
 * 
 * <p>该异常可包装底层的{@link java.sql.SQLException}或其他异常，提供错误消息和根源异常的关联，
 * 便于异常链的追踪与问题定位，是JDBC模块中统一的异常抛出入口。
 * 
 * @author soeasy.run
 */
public class JdbcException extends RuntimeException {

    /**
     * 序列化版本号，确保异常对象在序列化与反序列化过程中的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造默认的JDBC异常
     */
    public JdbcException() {
        super();
    }

    /**
     * 构造带错误消息的JDBC异常
     * 
     * @param message 异常详细消息，描述JDBC操作失败的原因（如"数据库连接超时"）
     */
    public JdbcException(String message) {
        super(message);
    }

    /**
     * 构造带错误消息和根源异常的JDBC异常
     * 
     * @param message 异常详细消息，说明错误场景
     * @param cause 导致该异常的根源异常（通常为{@link java.sql.SQLException}或其子类）
     */
    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造带根源异常的JDBC异常
     * 
     * @param cause 导致该异常的根源异常（用于包装底层JDBC错误，如SQL执行失败）
     */
    public JdbcException(Throwable cause) {
        super(cause);
    }
}