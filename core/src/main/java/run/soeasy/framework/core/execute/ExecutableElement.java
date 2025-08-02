package run.soeasy.framework.core.execute;

import lombok.NonNull;

/**
 * 可执行元素接口，继承自{@link ExecutableMetadata}和{@link ExecutableTemplate}，
 * 整合可执行元素的元数据描述与执行功能，是框架中表示方法、构造函数等可执行实体的核心接口。
 * <p>
 * 该接口提供了完整的可执行元素抽象，既包含参数类型、声明类型等元数据信息，
 * 又具备动态执行能力，支持参数类型校验和不同形式的执行调用。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>元数据整合：继承{@link ExecutableMetadata}获取可执行元素的完整元数据</li>
 *   <li>动态执行：继承{@link ExecutableTemplate}实现可执行元素的动态调用</li>
 *   <li>参数校验：默认实现包含参数类型匹配校验，避免类型不匹配异常</li>
 *   <li>执行重载：提供带参数类型和纯参数值两种执行方法，适应不同调用场景</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：作为反射方法或构造函数的统一抽象，实现动态调用</li>
 *   <li>框架核心组件：作为方法调用、依赖注入等框架功能的基础接口</li>
 *   <li>动态代理：为代理对象提供统一的方法执行接口</li>
 *   <li>插件系统：作为插件方法的抽象定义，支持动态加载和执行</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ExecutableMetadata
 * @see ExecutableTemplate
 */
public interface ExecutableElement extends ExecutableMetadata, ExecutableTemplate {

    /**
     * 带参数类型和值执行可执行元素（含参数校验）
     * <p>
     * 该默认实现先通过{@link #canExecuted(Class[])}方法校验参数类型匹配性，
     * 若不匹配则抛出{@link IllegalArgumentException}，否则调用纯参数值的执行方法。
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @param args 参数值数组，不可为null
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     * @throws IllegalArgumentException 参数类型不匹配时抛出
     */
    @Override
    default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
        if (!canExecuted(parameterTypes)) {
            throw new IllegalArgumentException("Parameter type mismatch");
        }

        return execute(args);
    }

    /**
     * 仅带参数值执行可执行元素（依赖元数据匹配参数类型）
     * <p>
     * 该方法通过可执行元素的元数据自动匹配参数类型，适用于参数类型已知或可推断的场景。
     * 实现类应根据元数据中的参数模板（{@link #getParameterTemplate()}）进行参数类型匹配和转换。
     * 
     * @param args 参数值数组，不可为null
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     * @see #getParameterTemplate()
     */
    Object execute(@NonNull Object... args) throws Throwable;
}