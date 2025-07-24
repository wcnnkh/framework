package run.soeasy.framework.core.execute;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 执行上下文包装器函数式接口，继承自{@link Execution}和{@link Wrapper}，
 * 采用装饰器模式对执行上下文进行包装，支持在不修改原始逻辑的前提下为执行过程添加额外功能。
 * <p>
 * 该接口作为函数式接口，允许通过Lambda表达式或方法引用来简洁地实现包装逻辑，
 * 适用于需要动态增强执行上下文行为的场景，如参数修改、结果处理、异常拦截等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始执行上下文并扩展其功能，符合"开闭原则"</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化包装逻辑的编码</li>
 *   <li>方法转发：默认方法将调用转发给被包装的源执行上下文，保持原始行为</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的执行上下文类型，需实现{@link Execution}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数修改：在执行前修改参数值（如敏感信息加密）</li>
 *   <li>结果处理：对执行结果进行转换或包装</li>
 *   <li>异常拦截：捕获执行异常并转换为自定义异常</li>
 *   <li>日志记录：记录执行过程的输入输出信息</li>
 *   <li>权限控制：验证当前调用者是否有权限执行</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Execution
 * @see Wrapper
 */
@FunctionalInterface
public interface ExecutionWrapper<W extends Execution> extends Execution, Wrapper<W> {

    /**
     * 获取被包装执行上下文的参数数组
     * <p>
     * 该默认实现将调用转发给被包装的源执行上下文的{@link Execution#getArguments()}方法，
     * 子类可覆盖此方法返回修改后的参数数组（如添加额外参数）。
     * 
     * @return 执行参数数组
     */
    @Override
    default Object[] getArguments() {
        return getSource().getArguments();
    }

    /**
     * 执行被包装的可执行元素
     * <p>
     * 该默认实现将调用转发给被包装的源执行上下文的{@link Execution#execute()}方法，
     * 子类可覆盖此方法添加前置处理（如参数校验）或后置处理（如结果加密）。
     * 
     * @return 执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    @Override
    default Object execute() throws Throwable {
        return getSource().execute();
    }

    /**
     * 获取被包装执行上下文的可执行元素元数据
     * <p>
     * 该默认实现将调用转发给被包装的源执行上下文的{@link Execution#getMetadata()}方法，
     * 子类可覆盖此方法返回包装后的元数据（如添加自定义元数据）。
     * 
     * @return 可执行元素元数据
     */
    @Override
    default ExecutableMetadata getMetadata() {
        return getSource().getMetadata();
    }
}