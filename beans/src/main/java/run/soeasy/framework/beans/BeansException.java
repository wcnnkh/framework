package run.soeasy.framework.beans;

/**
 * 框架中与Bean操作相关的通用异常类，继承自{@link RuntimeException}，
 * 用于封装Bean创建、属性访问、映射转换等过程中发生的异常，提供统一的异常处理入口。
 * 
 * <p>该异常适用于所有非检查型的Bean操作错误，如Bean实例化失败、属性反射访问异常、
 * 类型转换错误等场景，便于上层代码捕获并处理与Bean相关的异常。
 * 
 * @author soeasy.run
 */
public class BeansException extends RuntimeException {

    /**
     * 序列化版本号，确保异常对象在序列化与反序列化过程中的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造Bean异常（仅包含异常消息）
     * 
     * @param message 异常详细消息，描述异常发生的原因
     */
    public BeansException(String message) {
        super(message);
    }

    /**
     * 构造Bean异常（仅包含 cause 异常）
     * 
     * @param e 导致当前异常的根源异常（cause）
     */
    public BeansException(Throwable e) {
        super(e);
    }

    /**
     * 构造Bean异常（包含异常消息和 cause 异常）
     * 
     * @param message 异常详细消息
     * @param e 导致当前异常的根源异常
     */
    public BeansException(String message, Throwable e) {
        super(message, e);
    }
}