package run.soeasy.framework.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.Data;
import run.soeasy.framework.aop.ExecutionInterceptor;
import run.soeasy.framework.core.execute.CustomizeInvocation;
import run.soeasy.framework.core.execute.Invocation;

/**
 * JDK动态代理的调用处理器适配器，实现{@link InvocationHandler}和{@link Serializable}接口，
 * 用于将{@link ExecutionInterceptor}适配为JDK动态代理所需的调用处理器，
 * 是连接JDK代理机制与AOP拦截器框架的桥梁，实现拦截器对代理方法调用的增强。
 * 
 * <p>该类作为适配器，在JDK代理的方法被调用时，将JDK的调用参数（proxy、method、args）转换为AOP框架的{@link Invocation}对象，
 * 并委托给{@link ExecutionInterceptor}处理，从而将JDK代理的方法调用纳入AOP的拦截链体系。
 * 
 * @author soeasy.run
 * @see InvocationHandler
 * @see ExecutionInterceptor
 * @see Invocation
 */
@Data
final class ExecutionInterceptorToInvocationHandler implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * AOP拦截器，用于处理代理方法的调用增强逻辑（非空）
     */
    private final ExecutionInterceptor executionInterceptor;

    /**
     * 构造调用处理器适配器，绑定AOP拦截器
     * 
     * @param executionInterceptor 用于处理方法调用的AOP拦截器（非空）
     */
    public ExecutionInterceptorToInvocationHandler(ExecutionInterceptor executionInterceptor) {
        this.executionInterceptor = executionInterceptor;
    }

    /**
     * 处理JDK动态代理的方法调用，转换为AOP拦截器调用
     * 
     * <p>核心流程：
     * 1. 将JDK代理的方法（method）包装为{@link JdkProxyMethod}，适配AOP框架的方法描述；
     * 2. 创建{@link CustomizeInvocation}对象，封装方法、参数和代理对象（proxy）；
     * 3. 调用绑定的{@link ExecutionInterceptor#intercept(Execution)}方法，将调用交给AOP拦截链处理；
     * 4. 返回拦截器处理后的结果，完成方法调用增强。
     * 
     * @param proxy 代理对象（JDK动态生成的代理实例）
     * @param method 被调用的方法（代理对象实现的接口方法或继承的方法）
     * @param args 方法调用的参数数组（可为空）
     * @return 方法调用的结果（经拦截器处理后的返回值）
     * @throws Throwable 方法调用或拦截处理过程中抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 包装方法为JDK代理方法，创建调用上下文
        Invocation invocation = new CustomizeInvocation<>(new JdkProxyMethod(method), args);
        // 设置调用目标为代理对象（支持拦截器中访问代理实例）
        invocation.setTarget(proxy);
        // 委托拦截器处理调用
        return executionInterceptor.intercept(invocation);
    }
}