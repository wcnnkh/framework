package run.soeasy.framework.aop.jdk;

import java.lang.reflect.InvocationHandler;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.aop.ExecutionInterceptor;
import run.soeasy.framework.aop.Proxy;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * JDK动态代理实现类，实现{@link Proxy}接口，封装JDK动态代理的创建逻辑，
 * 用于基于JDK反射机制生成代理实例，并整合AOP拦截器链，是JDK代理模式在框架中的具体实现。
 * 
 * <p>该类通过封装{@link java.lang.reflect.Proxy}的使用细节，将AOP的{@link ExecutionInterceptor}
 * 与JDK代理的{@link InvocationHandler}适配，提供统一的代理创建入口，支持通过{@link #execute()}方法生成代理对象。
 * 
 * @author soeasy.run
 * @see Proxy
 * @see java.lang.reflect.Proxy
 * @see ExecutionInterceptorToInvocationHandler
 */
@Data
@RequiredArgsConstructor
public class JdkProxy implements Proxy {

    /**
     * 代理返回类型描述符（用于标识代理对象的类型信息）
     */
    private final TypeDescriptor returnTypeDescriptor;

    /**
     * 用于加载代理类的类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 代理对象需要实现的接口数组（可为空）
     */
    private final Class<?>[] interfaces;

    /**
     * JDK代理的调用处理器（用于处理代理方法的调用逻辑）
     */
    private final InvocationHandler invocationHandler;

    /**
     * 构造JDK代理实例，关联目标类、接口和AOP拦截器
     * 
     * <p>核心逻辑：
     * 1. 基于目标类创建返回类型描述符{@link TypeDescriptor}；
     * 2. 使用目标类的类加载器作为代理类的类加载器；
     * 3. 将AOP拦截器{@link ExecutionInterceptor}适配为JDK代理的{@link InvocationHandler}（通过{@link ExecutionInterceptorToInvocationHandler}）。
     * 
     * @param targetClass 被代理的目标类（非空，用于获取类加载器和类型信息）
     * @param interfaces 代理对象需要实现的接口数组（可为空）
     * @param executionInterceptor AOP拦截器（用于处理代理方法的增强逻辑，非空）
     */
    public JdkProxy(Class<?> targetClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
        this(
            TypeDescriptor.valueOf(targetClass),
            targetClass.getClassLoader(),
            interfaces,
            new ExecutionInterceptorToInvocationHandler(executionInterceptor)
        );
    }

    /**
     * 判断当前代理是否可执行（仅支持无参数调用）
     * 
     * <p>当前JDK代理的{@link #execute(Class[], Object...)}方法仅支持无参数调用，因此当参数类型数组长度为0时返回true。
     * 
     * @param parameterTypes 方法参数类型数组（非空）
     * @return 参数类型为空数组时返回true，否则返回false
     */
    @Override
    public boolean canExecuted(@NonNull Class<?>... parameterTypes) {
        return parameterTypes.length == 0;
    }

    /**
     * 执行代理创建逻辑，生成JDK动态代理实例
     * 
     * <p>行为限制：不支持带参数的调用（参数或参数类型数组长度不为0时抛出异常），
     * 因为该方法的核心作用是创建代理实例，而非执行代理对象的方法。
     * 
     * <p>核心流程：
     * 1. 校验参数，若存在参数则抛出{@link UnsupportedOperationException}；
     * 2. 调用{@link java.lang.reflect.Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)}生成代理实例；
     * 3. 返回生成的代理对象（实现了{@code interfaces}中定义的所有接口）。
     * 
     * @param parameterTypes 方法参数类型数组（必须为空数组）
     * @param args 实际参数数组（必须为空数组）
     * @return 生成的JDK动态代理实例
     * @throws UnsupportedOperationException 当存在参数或参数类型时抛出
     */
    @Override
    public final Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) {
        // 校验：不支持带参数的调用
        if (args.length != 0 || parameterTypes.length != 0) {
            throw new UnsupportedOperationException("Jdk proxy does not support calls with parameters");
        }
        // 创建并返回JDK动态代理实例
        return java.lang.reflect.Proxy.newProxyInstance(
            classLoader, 
            interfaces == null ? new Class<?>[0] : interfaces, 
            invocationHandler
        );
    }
}