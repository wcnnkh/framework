package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 自定义执行上下文实现类，继承自{@link AbstractExecution}，
 * 用于执行实现了{@link ExecutableElement}接口的可执行元素。
 * <p>
 * 该类通过委托模式将执行逻辑转发给具体的可执行元素，
 * 并提供了参数校验和执行异常处理的能力，确保执行过程的安全性和可靠性。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>参数校验：执行前自动验证参数类型与可执行元素的匹配性</li>
 *   <li>执行委托：将实际执行逻辑委托给封装的可执行元素</li>
 *   <li>异常处理：直接抛出执行过程中产生的异常，便于上层统一处理</li>
 *   <li>泛型支持：通过泛型约束确保元数据类型的一致性</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：动态执行Java方法或构造函数</li>
 *   <li>框架扩展：作为框架插件系统的执行器</li>
 *   <li>自定义命令：封装自定义命令的执行逻辑</li>
 *   <li>测试工具：用于单元测试中的方法调用</li>
 * </ul>
 * </p>
 *
 * @param <W> 可执行元素类型，需实现{@link ExecutableElement}接口
 * @author soeasy.run
 * @see AbstractExecution
 * @see ExecutableElement
 */
public class CustomizeExecution<W extends ExecutableElement> extends AbstractExecution<W> {

    /**
     * 构造函数，初始化执行上下文
     * 
     * @param metadata 可执行元素元数据，不可为null
     * @param arguments 执行参数数组，不可为null
     */
    public CustomizeExecution(@NonNull W metadata, @NonNull Object[] arguments) {
        super(metadata, arguments);
    }

    /**
     * 执行可执行元素
     * <p>
     * 该方法会调用封装的可执行元素的{@link ExecutableElement#execute(Object...)}方法，
     * 并传递当前上下文中的参数数组。执行前无需额外的参数类型校验，
     * 因为{@link ExecutableElement}的默认实现已包含参数类型校验逻辑。
     * </p>
     * 
     * @return 执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    @Override
    public Object execute() throws Throwable {
        return getMetadata().execute(getArguments());
    }
}