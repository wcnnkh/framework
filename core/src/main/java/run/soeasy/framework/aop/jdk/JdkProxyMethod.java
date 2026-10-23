package run.soeasy.framework.aop.jdk;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.aop.ProxyUtils;
import run.soeasy.framework.core.execute.reflect.ReflectionMethod;

/**
 * JDK动态代理的方法包装类，继承自{@link ReflectionMethod}，
 * 专门用于处理JDK代理场景下的方法调用，对基础方法（hashCode、toString、equals）提供默认实现，
 * 避免代理逻辑干扰对象的基础行为，确保代理对象的方法调用符合预期。
 * 
 * <p>该类重写了方法调用逻辑，当调用基础方法时，通过{@link ProxyUtils}提供默认实现，
 * 其他方法则沿用父类的反射调用逻辑，是JDK动态代理中方法调用的关键处理组件。
 * 
 * @author soeasy.run
 * @see ReflectionMethod
 * @see ProxyUtils
 * @see Method
 */
@Getter
public class JdkProxyMethod extends ReflectionMethod {
    private static final long serialVersionUID = 1L;

    /**
     * 构造JDK代理方法包装类
     * 
     * @param method 被包装的原始方法（非空，如代理对象的接口方法或父类方法）
     */
    public JdkProxyMethod(Method method) {
        super(method);
    }

    /**
     * 执行方法调用，对基础方法提供特殊处理
     * 
     * <p>核心逻辑：
     * 1. 对于基础方法（hashCode、toString、equals），调用{@link ProxyUtils#invokeIgnoreMethod(Object, Method, Object[])}提供默认实现；
     * 2. 其他方法则沿用父类的反射调用逻辑（通过反射执行目标方法）。
     * 
     * @param target 方法调用的目标对象（代理对象或原始对象）
     * @param parameterTypes 方法的参数类型数组（非空，用于匹配方法签名）
     * @param args 实际传入的参数数组（非空，数量与类型需与parameterTypes匹配）
     * @return 方法调用结果（基础方法返回默认值，其他方法返回反射调用结果）
     * @throws Throwable 方法调用过程中可能抛出的异常（反射调用或默认实现抛出的异常）
     */
    @Override
    public Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
        // 委托ProxyUtils处理基础方法，其他方法由父类处理反射调用
        return ProxyUtils.invokeIgnoreMethod(target, getSource(), args);
    }
}