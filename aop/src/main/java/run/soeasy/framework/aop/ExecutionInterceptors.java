package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 执行拦截器集合，实现{@link ExecutionInterceptor}接口，用于管理一组组拦截器并形成拦截链，
 * 将多个拦截器按顺序执行，简化多拦截器场景的配置与调用，是AOP框架中批量管理拦截器的工具类。
 * 
 * <p>该类持有一个拦截器元素集合（{@link Streamable}），在拦截执行时，通过创建{@link ExecutionInterceptorChain}
 * 将拦截器集合转换为拦截链，按迭代顺序依次执行每个拦截器，最终触发{@code nextChain}（目标执行逻辑），
 * 实现多拦截器的有序增强。
 * 
 * @author soeasy.run
 * @see ExecutionInterceptor
 * @see ExecutionInterceptorChain
 * @see Streamable
 */
@Data
public class ExecutionInterceptors implements ExecutionInterceptor {

    /**
     * 拦截器元素集合（非空），包含需要按序执行的拦截器
     */
    @NonNull
    private final Streamable<? extends ExecutionInterceptor> executionInterceptors;

    /**
     * 拦截链执行完毕后触发的下一个执行节点（通常为目标业务逻辑）
     */
    private Execution nextChain;

    /**
     * 拦截执行过程，创建拦截链并执行所有拦截器
     * 
     * <p>处理逻辑：
     * 1. 基于当前拦截器集合和下一个执行节点，创建{@link ExecutionInterceptorChain}；
     * 2. 调用拦截链的{@link ExecutionInterceptorChain#intercept(Execution)}方法，
     *    触发所有拦截器按序执行，最终执行{@code nextChain}。
     * 
     * @param function 执行上下文（非空，包含执行所需的信息）
     * @return 执行结果（经过所有拦截器处理后的目标逻辑返回值）
     * @throws Throwable 拦截过程中任意拦截器或目标逻辑抛出的异常
     */
    @Override
    public Object intercept(@NonNull Execution function) throws Throwable {
        // 创建拦截器链，关联当前拦截器集合与下一个执行节点
        ExecutionInterceptorChain chain = new ExecutionInterceptorChain(executionInterceptors.toList().iterator(), nextChain);
        // 执行拦截链
        return chain.intercept(function);
    }
}