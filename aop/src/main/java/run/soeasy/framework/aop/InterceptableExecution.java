package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.core.execute.ExecutionWrapper;

/**
 * 可被拦截的执行包装类，实现{@link ExecutionWrapper}接口，
 * 用于将执行对象（{@link Execution}）与拦截器（{@link ExecutionInterceptor}）绑定，
 * 在执行时触发拦截器的增强逻辑，是AOP框架中连接执行过程与拦截器的核心组件。
 * 
 * <p>该类通过包装原始执行对象和对应的拦截器，在调用{@link #execute()}方法时，
 * 会先触发拦截器的{@link ExecutionInterceptor#intercept(Execution)}方法，
 * 实现对原始执行过程的增强或控制，形成拦截链的基础节点。
 * 
 * @param <W> 被包装的执行对象类型（需继承自{@link Execution}）
 * @author soeasy.run
 * @see ExecutionWrapper
 * @see Execution
 * @see ExecutionInterceptor
 */
@Data
public class InterceptableExecution<W extends Execution> implements ExecutionWrapper<W> {

    /**
     * 原始执行对象（被包装的执行过程，非空）
     */
    @NonNull
    private final W source;

    /**
     * 关联的执行拦截器（用于增强或控制原始执行过程，非空）
     */
    @NonNull
    private final ExecutionInterceptor executionInterceptor;

    /**
     * 执行被包装的执行过程，触发拦截器逻辑
     * 
     * <p>调用关联的{@link ExecutionInterceptor#intercept(Execution)}方法，
     * 将原始执行对象{@code source}作为参数传入，由拦截器决定执行逻辑（如前置处理、执行原始过程、后置处理等）。
     * 
     * @return 执行结果（可能是原始执行过程的返回值，或经拦截器处理后的结果）
     * @throws Throwable 执行过程中可能抛出的异常（由原始执行过程或拦截器逻辑产生）
     */
    @Override
    public Object execute() throws Throwable {
        return executionInterceptor.intercept(source);
    }
}