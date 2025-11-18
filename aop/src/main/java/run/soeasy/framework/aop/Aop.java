package run.soeasy.framework.aop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.aop.jdk.JdkProxyFactory;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.execute.Execution;

/**
 * AOP核心组件，继承自{@link JdkProxyFactory}，提供基于JDK动态代理的AOP功能实现，
 * 整合拦截器注册、代理对象创建、执行增强等核心能力，是AOP框架的入口类，支持全局单例模式。
 * 
 * <p>该类通过UUID生成唯一标识ID，管理拦截器注册表，并在创建代理对象时自动整合：
 * 1. 被代理对象标识拦截器（{@link DelegatedObjectExecutionInterceptor}）；
 * 2. 全局注册的拦截器（{@link ExecutionInterceptorRegistry}）；
 * 3. 自定义拦截器，形成完整的拦截链，实现对目标对象或执行逻辑的增强。
 * 
 * <p>支持两种代理场景：
 * - 基于类的代理（创建实现指定接口的代理对象）；
 * - 基于执行逻辑的代理（对{@link Execution}进行拦截增强）。
 * 
 * @author soeasy.run
 * @see JdkProxyFactory
 * @see ExecutionInterceptorRegistry
 * @see DelegatedObject
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Aop extends JdkProxyFactory {

    /**
     * 全局AOP实例（单例模式）
     */
    private static volatile Aop global;

    /**
     * 获取全局AOP单例实例
     * 
     * <p>采用双重检查锁机制确保线程安全，首次调用时初始化并配置实例。
     * 
     * @return 全局唯一的AOP实例
     */
    public static Aop global() {
        if (global == null) {
            synchronized (Aop.class) {
                if (global == null) {
                    global = new Aop();
                    global.configure();
                }
            }
        }
        return global;
    }

    /**
     * 当前AOP实例的唯一标识ID（通过UUID生成）
     */
    private final String id;

    /**
     * 执行拦截器注册表，管理全局注册的拦截器
     */
    private final ExecutionInterceptorRegistry executionInterceptorRegistry = new ExecutionInterceptorRegistry();

    /**
     * 构造AOP实例，生成唯一标识ID
     */
    public Aop() {
        this.id = RandomUtils.uuid();
    }

    /**
     * 获取当前AOP实例的唯一标识ID（不可修改）
     * 
     * @return 实例ID
     */
    public final String getId() {
        return id;
    }

    /**
     * 判断对象是否为当前AOP实例创建的代理对象
     * 
     * <p>通过检查对象是否实现{@link DelegatedObject}接口，且其代理容器ID与当前实例ID一致。
     * 
     * @param instance 待判断的对象
     * @return 是当前AOP实例创建的代理对象则返回true，否则返回false
     */
    public boolean isProxy(Object instance) {
        if (instance instanceof DelegatedObject) {
            return StringUtils.equals(((DelegatedObject) instance).getProxyContainerId(), this.id);
        }
        return false;
    }

    /**
     * 创建代理对象，整合拦截器链
     * 
     * <p>核心逻辑：
     * 1. 创建{@link DelegatedObjectExecutionInterceptor}，注入当前实例ID，确保代理对象能正确返回容器ID；
     * 2. 合并全局注册的拦截器与自定义拦截器，形成完整拦截链；
     * 3. 为代理对象添加{@link DelegatedObject}接口，使其具备代理标识能力；
     * 4. 调用父类（JdkProxyFactory）的代理创建方法生成最终代理对象。
     * 
     * @param sourceClass 被代理的原始类（非空）
     * @param interfaces 代理对象需要实现的额外接口（可为空）
     * @param executionInterceptor 自定义拦截器（可为空）
     * @return 生成的代理对象（实现{@link Proxy}和{@link DelegatedObject}接口）
     */
    @Override
    public Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces,
            ExecutionInterceptor executionInterceptor) {
        // 创建被代理对象标识拦截器，绑定当前AOP实例ID
        DelegatedObjectExecutionInterceptor delegatedObjectExecutionInterceptor = new DelegatedObjectExecutionInterceptor(
                this.id);
        
        // 合并拦截器：标识拦截器 + 全局注册拦截器 + 自定义拦截器
        Elements<? extends ExecutionInterceptor> executionInterceptors;
        if (executionInterceptor == null) {
            executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
                    getExecutionInterceptorRegistry());
        } else {
            executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
                    getExecutionInterceptorRegistry(), executionInterceptor);
        }
        
        // 包装为拦截器集合
        ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);

        // 合并接口：确保代理对象实现DelegatedObject接口
        Class<?>[] useInterfaces = new Class<?>[] { DelegatedObject.class };
        if (interfaces != null) {
            useInterfaces = ArrayUtils.merge(useInterfaces, interfaces);
        }
        
        // 调用父类方法创建JDK动态代理
        return super.getProxy(sourceClass, useInterfaces, useExecutionInterceptor);
    }

    /**
     * 创建代理对象（无额外接口和自定义拦截器）
     * 
     * @param sourceClass 被代理的原始类（非空）
     * @return 生成的代理对象
     */
    public final Proxy getProxy(@NonNull Class<?> sourceClass) {
        return getProxy(sourceClass, null, null);
    }

    /**
     * 为执行逻辑创建代理（无自定义拦截器）
     * 
     * @param function 待代理的执行逻辑（非空）
     * @return 增强后的执行对象
     */
    public final Execution getProxyFunction(@NonNull Execution function) {
        return getProxyFunction(function, null);
    }

    /**
     * 为执行逻辑创建代理，添加拦截增强
     * 
     * <p>为{@link Execution}对象创建拦截包装，整合标识拦截器、全局拦截器和自定义拦截器，
     * 实现对执行逻辑的增强。
     * 
     * @param execution 待代理的执行逻辑（非空）
     * @param executionInterceptor 自定义拦截器（可为空）
     * @return 增强后的执行对象
     */
    public Execution getProxyFunction(@NonNull Execution execution, ExecutionInterceptor executionInterceptor) {
        // 创建被代理对象标识拦截器
        DelegatedObjectExecutionInterceptor delegatedObjectExecutionInterceptor = new DelegatedObjectExecutionInterceptor(
                this.id);
        
        // 合并拦截器
        Elements<? extends ExecutionInterceptor> executionInterceptors;
        if (executionInterceptor == null) {
            executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
                    getExecutionInterceptorRegistry());
        } else {
            executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
                    getExecutionInterceptorRegistry(), executionInterceptor);
        }
        
        // 包装为拦截器集合并创建可拦截的执行对象
        ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);
        return new InterceptableExecution<>(execution, useExecutionInterceptor);
    }

    /**
     * 为具体实例创建代理对象（无额外接口和自定义拦截器）
     * 
     * @param <T> 实例类型
     * @param sourceClass 实例的类（非空）
     * @param source 具体实例（被代理的目标对象）
     * @return 生成的代理对象
     */
    public final <T> Proxy getProxy(Class<? extends T> sourceClass, T source) {
        return getProxy(sourceClass, source, null, null);
    }

    /**
     * 为具体实例创建代理对象，支持动态切换目标实例
     * 
     * <p>核心逻辑：
     * 1. 创建{@link SwitchableTargetInvocationInterceptor}，绑定目标实例，支持动态切换；
     * 2. 合并切换目标拦截器与自定义拦截器；
     * 3. 调用{@link #getProxy(Class, Class[], ExecutionInterceptor)}生成代理对象。
     * 
     * @param <T> 实例类型
     * @param sourceClass 实例的类（非空）
     * @param source 具体实例（被代理的目标对象）
     * @param interfaces 代理对象需要实现的额外接口（可为空）
     * @param executionInterceptor 自定义拦截器（可为空）
     * @return 生成的代理对象
     */
    public <T> Proxy getProxy(@NonNull Class<? extends T> sourceClass, T source, Class<?>[] interfaces,
            ExecutionInterceptor executionInterceptor) {
        // 创建可切换目标的拦截器，绑定当前实例
        SwitchableTargetInvocationInterceptor switchableTargetExecutionInterceptor = new SwitchableTargetInvocationInterceptor(
                source);
        
        // 合并拦截器：切换目标拦截器 + 自定义拦截器
        Elements<? extends ExecutionInterceptor> executionInterceptors;
        if (executionInterceptor == null) {
            executionInterceptors = Elements.forArray(switchableTargetExecutionInterceptor);
        } else {
            executionInterceptors = Elements.forArray(switchableTargetExecutionInterceptor, executionInterceptor);
        }
        
        // 包装为拦截器集合并创建代理
        ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);
        return getProxy(sourceClass, interfaces, useExecutionInterceptor);
    }

    /**
     * 返回当前AOP实例的ID（重写toString用于标识）
     * 
     * @return 实例ID
     */
    @Override
    public String toString() {
        return id;
    }
}