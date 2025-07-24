package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.core.execute.Invocation;

/**
 * 方法调用拦截器接口，继承自{@link ExecutionInterceptor}，专门用于拦截方法调用类型的执行过程，
 * 是AOP框架中针对方法调用场景的核心拦截契约，专注于对{@link Invocation}（方法调用上下文）的增强处理。
 * 
 * <p>该接口通过默认方法实现了对{@link Execution}的适配：
 * 1. 当执行上下文为{@link Invocation}（方法调用场景）时，转发至{@link #intercept(Invocation)}进行处理；
 * 2. 对于非方法调用的执行场景，保持默认的拦截流程（理论上不触发，仅作为兼容性处理）。
 * 
 * <p>实现类可通过该接口专注于方法调用的增强逻辑，例如：
 * - 方法参数校验与转换
 * - 调用前后的日志记录
 * - 事务管理（开启、提交、回滚）
 * - 异常捕获与处理
 * - 性能监控与统计
 * 
 * @author soeasy.run
 * @see ExecutionInterceptor
 * @see Invocation
 */
public interface InvocationInterceptor extends ExecutionInterceptor {

    /**
     * 拦截执行过程（默认实现，实现对方法调用场景的适配）
     * 
     * <p>此默认方法将通用执行上下文转换为方法调用上下文，确保方法调用场景被正确拦截处理，
     * 非方法调用场景则沿用默认拦截流程（实际应用中通常无需处理）。
     * 
     * @param execution 执行上下文（非空，可能是方法调用或其他类型的执行）
     * @return 执行结果（目标逻辑的返回值或增强后的结果）
     * @throws Throwable 拦截过程中可能抛出的异常
     */
    @Override
    default Object intercept(@NonNull Execution execution) throws Throwable {
        if (execution instanceof Invocation) {
            // 方法调用场景，转发至专门的拦截方法
            return intercept((Invocation) execution);
        }
        // 非方法调用场景，默认继续执行（理论上不进入此分支）
        return intercept(execution);
    }

    /**
     * 拦截方法调用（核心方法，需由实现类提供具体增强逻辑）
     * 
     * <p>实现类通过此方法对方法调用进行增强，可通过{@link Invocation}获取调用详情（目标对象、方法、参数等），
     * 并通过{@link Invocation#proceed()}触发后续执行（如调用目标方法或下一个拦截器）。
     * 
     * @param invocation 方法调用上下文（非空，包含方法调用的完整信息）
     * @return 方法调用结果（目标方法的返回值或经拦截器处理后的结果）
     * @throws Throwable 拦截过程中可能抛出的异常（如业务异常、权限异常等）
     */
    Object intercept(@NonNull Invocation invocation) throws Throwable;
}