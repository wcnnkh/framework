package run.soeasy.framework.core.execute;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptorWrapper;

/**
 * 可执行描述符包装器函数式接口，继承自{@link ExecutableDescriptor}、
 * {@link SourceDescriptorWrapper}和{@link AnnotatedElementWrapper}，
 * 用于对可执行描述符进行增强或扩展，支持访问原始描述符并添加额外功能。
 * <p>
 * 该接口采用装饰器模式，允许在不修改原始描述符的情况下，为其提供额外行为。
 * 作为函数式接口，它可以通过Lambda表达式或方法引用简洁地实现。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装原始描述符提供增强功能</li>
 *   <li>函数式接口：支持Lambda表达式实现，简化代码</li>
 *   <li>透明代理：默认方法转发调用至原始描述符</li>
 *   <li>类型安全：泛型约束确保包装器与被包装对象类型兼容</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>日志增强：在执行可执行元素前后添加日志记录</li>
 *   <li>权限控制：在执行前检查访问权限</li>
 *   <li>性能监控：测量可执行元素的执行时间</li>
 *   <li>参数验证：在执行前验证参数合法性</li>
 * </ul>
 *
 * @param <W> 被包装的可执行描述符类型
 * 
 * @author soeasy.run
 * @see ExecutableDescriptor
 * @see SourceDescriptorWrapper
 * @see AnnotatedElementWrapper
 */
@FunctionalInterface
public interface ExecutableDescriptorWrapper<W extends ExecutableDescriptor>
        extends ExecutableDescriptor, SourceDescriptorWrapper<W>, AnnotatedElementWrapper<W> {

    /**
     * 判断被包装的可执行元素是否可以无参执行
     * <p>
     * 该默认方法将调用转发给原始描述符的{@link ExecutableDescriptor#canExecuted()}方法。
     * 
     * @return 若被包装的可执行元素可以无参执行返回true，否则返回false
     */
    @Override
    default boolean canExecuted() {
        return getSource().canExecuted();
    }

    /**
     * 判断被包装的可执行元素是否可以使用指定参数类型执行
     * <p>
     * 该默认方法将调用转发给原始描述符的{@link ExecutableDescriptor#canExecuted(Class[])}方法。
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @return 若被包装的可执行元素可以接受指定参数类型返回true，否则返回false
     */
    @Override
    default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
        return getSource().canExecuted(parameterTypes);
    }

    /**
     * 获取被包装的可执行元素的返回类型描述符
     * <p>
     * 该默认方法将调用转发给原始描述符的{@link ExecutableDescriptor#getReturnTypeDescriptor()}方法。
     * 
     * @return 返回类型描述符
     */
    @Override
    default TypeDescriptor getReturnTypeDescriptor() {
        return getSource().getReturnTypeDescriptor();
    }
}