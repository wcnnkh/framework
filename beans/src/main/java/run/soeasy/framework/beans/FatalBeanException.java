package run.soeasy.framework.beans;

/**
 * 致命的Bean异常，继承自{@link BeansException}，用于表示Bean操作过程中发生的严重且无法恢复的错误，
 * 通常意味着底层配置错误、核心组件失效或关键元数据解析失败，需要开发者介入处理（如修复代码或配置）。
 * 
 * <p>与普通{@link BeansException}相比，该异常强调错误的严重性，通常发生在Bean初始化、内省解析等基础环节，
 * 例如无法获取BeanInfo、关键属性缺失或类型不匹配导致的核心功能中断等场景。
 * 
 * @author soeasy.run
 * @see BeansException
 */
public class FatalBeanException extends BeansException {

    /**
     * 序列化版本号，确保异常对象在序列化与反序列化过程中的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造致命Bean异常（仅包含异常消息）
     * 
     * @param msg 详细错误消息，描述导致致命错误的具体原因（如"无法解析Bean类的属性信息，导致初始化失败"）
     */
    public FatalBeanException(String msg) {
        super(msg);
    }

    /**
     * 构造致命Bean异常（包含错误消息和根源异常）
     * 
     * @param msg   详细错误消息，说明异常场景
     * @param cause 导致该致命错误的根源异常（如{@link java.beans.IntrospectionException}、反射异常等）
     */
    public FatalBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}