package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 可执行模板接口，继承自{@link ExecutableDescriptor}，定义了可执行元素（方法、构造函数等）的执行规范，
 * 支持根据参数类型和参数值动态执行可执行元素，并处理执行过程中可能抛出的异常。
 * <p>
 * 该接口是框架中动态执行的核心接口，通过抽象执行逻辑，使不同类型的可执行元素（如反射方法、动态代理方法等）
 * 能够遵循统一的执行规范，实现执行逻辑的解耦和复用。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>无参执行：通过{@link #execute()}方法支持无参数的可执行元素调用</li>
 *   <li>带参执行：通过{@link #execute(Class[], Object...)}方法支持指定参数类型和值的执行</li>
 *   <li>异常处理：直接抛出执行过程中产生的原始异常，便于上层调用者处理</li>
 *   <li>类型安全：通过参数类型数组确保参数与可执行元素的兼容性</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：动态调用对象的方法或构造函数</li>
 *   <li>代理执行：在代理对象中转发方法调用</li>
 *   <li>插件系统：动态加载并执行插件的功能方法</li>
 *   <li>动态语言接口：为动态语言与Java互操作提供统一执行接口</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see ExecutableDescriptor
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 */
public interface ExecutableTemplate extends ExecutableDescriptor {
    
    /**
     * 无参数执行可执行元素
     * <p>
     * 该默认实现调用{@link #execute(Class[], Object...)}方法，
     * 传入空参数类型数组和空参数值数组，适用于无参数的可执行元素调用。
     * </p>
     * 
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     */
    default Object execute() throws Throwable {
        return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
    }

    /**
     * 带参数执行可执行元素
     * <p>
     * 该方法是可执行元素的核心执行方法，需要传入参数类型数组和参数值数组：
     * <ul>
     *   <li>参数类型数组用于匹配可执行元素的参数签名</li>
     *   <li>参数值数组包含实际传入的参数值</li>
     * </ul>
     * 执行前会检查参数类型与可执行元素的兼容性（通过{@link #canExecuted(Class[])}方法），
     * 若不兼容则可能抛出异常（具体实现决定）。
     * </p>
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @param args 参数值数组，不可为null
     * @return 可执行元素的执行结果
     * @throws Throwable 执行过程中抛出的任何异常
     * @see #canExecuted(Class[])
     */
    Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;
}