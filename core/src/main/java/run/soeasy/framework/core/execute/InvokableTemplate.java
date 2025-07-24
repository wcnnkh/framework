package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 可调用模板接口，继承自{@link ExecutableDescriptor}，定义了基于目标对象的可执行元素调用规范，
 * 支持通过反射机制动态调用目标对象的方法或构造函数，并处理调用过程中可能抛出的异常。
 * <p>
 * 该接口是框架中反射调用的核心抽象，通过统一的调用接口屏蔽了方法和构造函数的差异，
 * 使调用逻辑更加简洁和统一，同时提供了参数类型匹配和动态调用的能力。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>目标对象调用：支持对指定目标对象的方法进行调用</li>
 *   <li>参数类型匹配：通过参数类型数组确保调用参数与目标方法的兼容性</li>
 *   <li>动态执行：基于反射机制实现方法的动态调用</li>
 *   <li>异常处理：直接抛出调用过程中产生的原始异常，便于上层处理</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>框架反射调用：在运行时动态调用对象的方法</li>
 *   <li>代理模式实现：在代理对象中转发方法调用到目标对象</li>
 *   <li>插件系统：动态加载并执行插件的功能方法</li>
 *   <li>依赖注入：通过反射调用构造函数或设置方法完成对象初始化</li>
 *   <li>命令模式：将方法调用封装为命令对象，支持撤销、记录等操作</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see ExecutableDescriptor
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 */
public interface InvokableTemplate extends ExecutableDescriptor {
    
    /**
     * 无参数调用目标对象的可执行元素
     * <p>
     * 该默认实现调用{@link #invoke(Object, Class[], Object...)}方法，
     * 传入空参数类型数组和空参数值数组，适用于无参数的方法或构造函数调用。
     * </p>
     * 
     * @param target 目标对象，对于静态方法可为null
     * @return 可执行元素的调用结果
     * @throws Throwable 调用过程中抛出的任何异常
     */
    default Object invoke(Object target) throws Throwable {
        return invoke(target, ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
    }

    /**
     * 带参数调用目标对象的可执行元素
     * <p>
     * 该方法是可执行元素的核心调用方法，需要传入目标对象、参数类型数组和参数值数组：
     * <ul>
     *   <li>目标对象：要调用方法的对象实例，对于静态方法可为null</li>
     *   <li>参数类型数组：用于匹配可执行元素的参数签名</li>
     *   <li>参数值数组：包含实际传入的参数值</li>
     * </ul>
     * 调用前会检查参数类型与可执行元素的兼容性（通过{@link #canExecuted(Class[])}方法），
     * 若不兼容则可能抛出异常（具体实现决定）。
     * </p>
     * 
     * @param target 目标对象，对于静态方法可为null
     * @param parameterTypes 参数类型数组，不可为null
     * @param args 参数值数组，不可为null
     * @return 可执行元素的调用结果
     * @throws Throwable 调用过程中抛出的任何异常
     * @see #canExecuted(Class[])
     */
    Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;
}