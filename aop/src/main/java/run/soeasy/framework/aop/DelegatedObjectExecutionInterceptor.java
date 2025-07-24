package run.soeasy.framework.aop;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execute.Invocation;

/**
 * 被代理对象执行拦截器，实现{@link InvocationInterceptor}接口和{@link Serializable}接口，
 * 专门用于处理{@link DelegatedObject}接口中定义的{@link DelegatedObject#getProxyContainerId()}方法调用，
 * 为代理对象提供预设的代理容器ID返回逻辑，确保被代理对象能正确返回其所属代理容器的标识。
 * 
 * <p>该拦截器在拦截方法调用时，会判断当前调用是否为获取代理容器ID的方法，若是则直接返回预设的ID，
 * 否则执行原始调用逻辑，从而在不修改目标对象的情况下，为代理对象注入{@code getProxyContainerId()}的实现。
 * 
 * @author soeasy.run
 * @see InvocationInterceptor
 * @see DelegatedObject
 * @see Invocation
 */
@Data
class DelegatedObjectExecutionInterceptor implements InvocationInterceptor, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 代理容器的唯一标识ID（预设值，用于返回给{@link DelegatedObject#getProxyContainerId()}调用）
     */
    private final String id;

    /**
     * 拦截方法调用，处理代理容器ID的获取请求
     * 
     * <p>处理逻辑：
     * 1. 判断当前调用的方法是否为{@link DelegatedObject#PROXY_CONTAINER_ID_METHOD_NAME}（即"getProxyContainerId"）；
     * 2. 若方法匹配且无参数，则直接返回预设的代理容器ID（{@code id}）；
     * 3. 若方法不匹配或有参数，则调用{@link Invocation#execute()}执行原始调用逻辑。
     * 
     * @param executor 方法调用上下文（非空，包含方法调用的详细信息）
     * @return 方法调用结果（预设的代理容器ID，或原始方法的返回值）
     * @throws Throwable 原始方法调用可能抛出的异常
     */
    @Override
    public Object intercept(@NonNull Invocation executor) throws Throwable {
        // 判断是否为获取代理容器ID的方法调用（方法名匹配且无参数）
        if (executor.getMetadata().getParameterTemplate().isEmpty()
                && executor.getMetadata().getName().equals(DelegatedObject.PROXY_CONTAINER_ID_METHOD_NAME)) {
            return id; // 返回预设的代理容器ID
        }
        // 非目标方法，执行原始调用
        return executor.execute();
    }

}