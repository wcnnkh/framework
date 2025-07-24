package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;

/**
 * 执行拦截器接口，作为函数式接口定义了对执行过程（{@link Execution}）的拦截契约，
 * 用于在目标执行逻辑前后插入增强处理（如日志记录、权限校验、异常处理等），是AOP框架中实现横切关注点的核心接口。
 * 
 * <p>该接口通过{@link #intercept(Execution)}方法拦截执行过程，实现类可在该方法中：
 * 1. 执行前置处理（如参数验证、日志打印）；
 * 2. 调用{@link Execution#proceed()}继续执行后续流程（目标逻辑或下一个拦截器）；
 * 3. 执行后置处理（如结果加工、异常捕获），从而实现对执行过程的增强。
 * 
 * @author soeasy.run
 * @see Execution
 */
@FunctionalInterface
public interface ExecutionInterceptor {

    /**
     * 拦截执行过程并进行增强处理
     * 
     * <p>实现类需在此方法中定义拦截逻辑，通过参数{@code execution}可访问执行上下文（如目标对象、方法、参数等），
     * 并通过调用{@link Execution#proceed()}触发后续执行流程。若不调用proceed()，则会中断执行链，阻止目标逻辑执行。
     * 
     * @param execution 执行上下文对象，包含执行所需的所有信息（非空）
     * @return 执行结果（可能是目标逻辑的返回值，或经拦截器处理后的结果）
     * @throws Throwable 拦截过程中可能抛出的异常（由具体拦截逻辑决定）
     */
    Object intercept(@NonNull Execution execution) throws Throwable;
}