package run.soeasy.framework.core.execute;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 自定义方法调用上下文实现类，继承自{@link AbstractExecution}并实现{@link Invocation}接口，
 * 用于执行基于目标对象的可调用元素（如实例方法）。
 * <p>
 * 该类通过委托模式将调用逻辑转发给具体的可调用元素，
 * 并提供了目标对象管理、参数校验和执行异常处理的能力，确保调用过程的安全性和可靠性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>目标对象管理：通过getter/setter方法动态管理调用的目标对象</li>
 *   <li>参数校验：执行前自动验证参数类型与可调用元素的匹配性</li>
 *   <li>执行委托：将实际调用逻辑委托给封装的可调用元素</li>
 *   <li>异常处理：直接抛出执行过程中产生的异常，便于上层统一处理</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：动态执行Java对象的实例方法</li>
 *   <li>AOP实现：作为切面增强的方法调用上下文</li>
 *   <li>RPC框架：封装远程方法调用的上下文信息</li>
 *   <li>插件系统：动态加载并执行插件类的方法</li>
 * </ul>
 *
 * @param <W> 可调用元素类型，需实现{@link InvodableElement}接口
 * @author soeasy.run
 * @see AbstractExecution
 * @see Invocation
 * @see InvodableElement
 */
@Getter
@Setter
public class CustomizeInvocation<W extends InvodableElement> extends AbstractExecution<W> implements Invocation {
    /**
     * 方法调用的目标对象，即方法所属的实例对象
     */
    private Object target;

    /**
     * 构造函数，初始化方法调用上下文
     * 
     * @param metadata 可调用元素元数据，不可为null
     * @param arguments 执行参数数组，不可为null
     */
    public CustomizeInvocation(@NonNull W metadata, @NonNull Object[] arguments) {
        super(metadata, arguments);
    }

    /**
     * 执行目标对象的方法
     * <p>
     * 该方法会调用封装的可调用元素的{@link InvodableElement#invoke(Object, Object...)}方法，
     * 并传递当前上下文中的目标对象和参数数组。执行前无需额外的参数类型校验，
     * 因为{@link InvodableElement}的默认实现已包含参数类型校验逻辑。
     * 
     * @return 方法执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    @Override
    public Object execute() throws Throwable {
        return getMetadata().invoke(getTarget(), getArguments());
    }
}